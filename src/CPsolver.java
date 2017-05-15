import ilog.concert.*;
import ilog.cp.*;

import java.io.*;
import java.util.*;


public class CPsolver {

	static IloIntExpr[] arrayFromList(List<IloIntExpr> list) {
	    return (IloIntExpr[]) list.toArray(new IloIntExpr[list.size()]);
	}
	
	static IloNumExpr[] arrayFromList_2(List<IloNumExpr> list) {
	    return (IloNumExpr[]) list.toArray(new IloNumExpr[list.size()]);
	}
	
	static class IntervalVarList extends ArrayList<IloIntervalVar> {
		public IloIntervalVar[] toArray() {
			return (IloIntervalVar[]) this.toArray(new IloIntervalVar[this.size()]);
		}
	}
	
	private List<Service> services;
	private double alpha;
	private List<TenantC> tenants;
	private List<Double> Logistic;
	private List<Double> Delay;
	
	private double objValue, objDelay, objLogistic;
	
	private double solve_time;
	
	public double getSolve_time() {
		return solve_time;
	}

	public void setSolve_time(double solve_time) {
		this.solve_time = solve_time;
	}

	public CPsolver(double alpha) {
		this.setAlpha(alpha);
		this.Logistic = new ArrayList<Double>();
		this.Delay = new ArrayList<Double>();
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public void solve(List<TenantC> tC) throws IloException {
		IloCP cp = new IloCP();
		List<List<IntervalVarList>> resourceMember = new  ArrayList<List<IntervalVarList>>();
		
		for (Service service : services) {
			List<IntervalVarList> serviceMember = new ArrayList<IntervalVarList>();
			for (Resource res : service.getResources()) {
				IntervalVarList resource = new IntervalVarList();
				serviceMember.add(resource);
			}
			resourceMember.add(serviceMember);
		}
		
		List<List<IloIntExpr>> Tends = new ArrayList<List<IloIntExpr>>();
		List<List<IloNumExpr[]>> Logistics = new ArrayList<List<IloNumExpr[]>>();
		List<List<IloIntervalVar>> Doctors = new ArrayList<List<IloIntervalVar>>();
		for (TenantC tenantC : tC) {
			List<IloIntExpr> ends = new ArrayList<IloIntExpr>();
			List<IloNumExpr[]> Logs = new ArrayList<IloNumExpr[]>();
			List<IloIntervalVar> masters = new ArrayList<IloIntervalVar>();
			for (TenantS tS : tenantC.getTenants()) {
				if (tS.getProcessing() > 0) {
					IloIntervalVar master = cp.intervalVar(tS.getProcessing());
					Service s = services.get(tS.getServicetype());
					tS.setDistance(s);
					IntervalVarList members = new IntervalVarList();
					IloNumExpr[] log = new IloNumExpr[s.getAmount()];
					for (int i = 0; i < s.getAmount(); i++) {
						IloIntervalVar member = cp.intervalVar(tS.getProcessing());
						member.setOptional();
						members.add(member);
						resourceMember.get(s.getId()).get(i).add(member);
						log[i] = (cp.prod(cp.presenceOf(member), tS.getDistance().get(i)));
					}
					Logs.add(log);
					cp.add(cp.alternative(master, members.toArray()));
					cp.add(cp.ge(cp.startOf(master), tenantC.getRelease()));
					masters.add(master);
					ends.add(cp.endOf(master));
				}
			}
			Doctors.add(masters);
			Logistics.add(Logs);
			Tends.add(ends);
			for (TenantS tS : tenantC.getTenants()) {
				if (tS.getProcessing() >0) {
					for (TenantS succT : tenantC.getSuccessors(tS.getId())) {
						if (succT.isFinal() == false) {
							cp.add(cp.le(   cp.sum(  cp.endOf(masters.get(tS.getId()-1)), cp.sum(Logs.get(tS.getId()-1))  ) , cp.startOf(masters.get(succT.getId()-1))  ));
						}
					}
				}
			}
			
		}
		
		for (List<IntervalVarList> list : resourceMember) {
			for (IntervalVarList intervalVarList : list) {
				cp.add(cp.noOverlap(intervalVarList.toArray()));
			}
		}
		
		List<IloIntExpr> delaies = new ArrayList<IloIntExpr>();
		List<IloIntVar> dels = new ArrayList<IloIntVar>();
		for (TenantC tenantC : tC) {
//			IloIntExpr temp = cp.intExpr();
			IloIntVar temp2 = cp.intVar(-Integer.MAX_VALUE, Integer.MAX_VALUE);
			delaies.add(cp.diff(cp.diff(cp.max(Tends.get(tenantC.getId())),tenantC.getRelease()),  tenantC.getMPM_time()));
			cp.add(cp.eq(temp2,cp.diff(cp.diff(cp.max(Tends.get(tenantC.getId())),tenantC.getRelease()),tenantC.getMPM_time())));
			dels.add(temp2);
		}
		
		List<IloNumVar> logs = new ArrayList<IloNumVar>();
		List<IloNumExpr> Log = new ArrayList<IloNumExpr>();
		
		for (List<IloNumExpr[]> lllog : Logistics) {
			IloNumVar tmp = cp.numVar(0, Double.MAX_VALUE);
			IloNumExpr[] temp = cp.numExprArray(lllog.size());
			for (int i = 0; i< lllog.size(); i++) {
				temp[i] = cp.sum(lllog.get(i));
			}
			Log.add(cp.sum(temp));
			cp.add(cp.eq(tmp,cp.sum(temp)));
			logs.add(tmp);			
		}
		
		IloNumExpr obj_log = cp.sum(arrayFromList_2(Log));
		IloNumExpr obj_delay = cp.sum(arrayFromList(delaies));
		int n = tC.size();
		IloObjective obj = cp.minimize(cp.sum(cp.prod((1-alpha)/n, obj_delay),cp.prod(alpha/n, obj_log)));
		
		cp.add(obj);
		cp.setParameter(IloCP.IntParam.FailLimit, 100);
//		cp.setOut(null);
		if (cp.solve()) {
			this.setSolve_time(cp.getInfo(IloCP.DoubleInfo.SolveTime));
			for (TenantC  tenantC : tC) {
				this.Delay.add(cp.getValue(dels.get(tenantC.getId())));
				this.Logistic.add(cp.getValue(logs.get(tenantC.getId())));
			}
			this.setObjValue(cp.getObjValue());
			this.setObjDelay(cp.getValue(obj_delay));
			this.setObjLogistic(cp.getValue(obj_log));
//			System.out.println("Time is:" + IloCP.ParameterValues.ElapsedTime);
		}
	}
	
	

	public List<TenantC> getTenants() {
		return tenants;
	}

	public void setTenants(List<TenantC> tenants) {
		this.tenants = tenants;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public List<Double> getLogistic() {
		return Logistic;
	}

	public List<Double> getDelay() {
		return Delay;
	}

	public double getObjValue() {
		return objValue;
	}

	public void setObjValue(double objValue) {
		this.objValue = objValue;
	}

	public double getObjDelay() {
		return objDelay;
	}

	public void setObjDelay(double objDelay) {
		this.objDelay = objDelay;
	}

	public double getObjLogistic() {
		return objLogistic;
	}

	public void setObjLogistic(double objLogistic) {
		this.objLogistic = objLogistic;
	}

}
