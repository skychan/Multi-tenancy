import ilog.concert.*;
import ilog.cp.*;

import java.io.*;
import java.util.*;


public class CPsolver {

	static IloIntExpr[] arrayFromList(List<IloIntExpr> list) {
	    return (IloIntExpr[]) list.toArray(new IloIntExpr[list.size()]);
	}
	static class IntervalVarList extends ArrayList<IloIntervalVar> {
		public IloIntervalVar[] toArray() {
			return (IloIntervalVar[]) this.toArray(new IloIntervalVar[this.size()]);
		}
	}
	
	private List<Service> services;
	private double alpha;
	private List<TenantC> tenants;
	
	public CPsolver(double alpha) {
		this.setAlpha(alpha);
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public double solve(List<TenantC> tC) throws IloException {
		IloCP cp = new IloCP();
//	    List<IloIntExpr> ends = new ArrayList<IloIntExpr>();
		
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
					cp.ge(cp.startOf(master), tenantC.getRelease());
					masters.add(master);
					ends.add(cp.endOf(master));
				}
			}
			Logistics.add(Logs);
			Tends.add(ends);
			for (TenantS tS : tenantC.getTenants()) {
				if (tS.getProcessing() >0) {
					for (TenantS succT : tenantC.getSuccessors(tS.getId())) {
						if (succT.isFinal() == false) {
							cp.add(cp.endBeforeStart(masters.get(tS.getId()-1), masters.get(succT.getId()-1)));
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
		for (TenantC tenantC : tC) {
			delaies.add(cp.diff(cp.diff(cp.max(Tends.get(tenantC.getId())),tenantC.getRelease()), tenantC.getMPM_time()));
		}
		
		IloNumExpr obj_log = cp.numExpr();
//		Logistics.toArray();
		for (List<IloNumExpr[]> lllog : Logistics) {
			IloNumExpr temp = cp.numExpr();
			for (IloNumExpr[] llog : lllog) {
				cp.eq(temp, cp.max(llog));
			}
			cp.eq(obj_log, cp.sum(obj_log,temp));
		}
		
		IloNumExpr obj_delay = cp.sum(arrayFromList(delaies));
		int n = tC.size();
		IloObjective obj = cp.minimize(cp.sum(cp.prod((1-alpha)/n, obj_delay),cp.prod(alpha, obj_log)));
		
		
		cp.add(obj);
		cp.setParameter(IloCP.IntParam.FailLimit, 3000);
		if (cp.solve()) {
			return cp.getObjValue();
		} else {
			return 0.0;
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

}
