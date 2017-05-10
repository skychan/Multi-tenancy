import java.io.*;
import java.util.*;

public class TenantC extends Tenant {

	private int nbTenants, nbServices;
	private int[] processings;
	private List<List<Integer>> successors;
	private List<TenantS> tenants;
	private Map<Integer,List<Integer>> predecessors;
	private int start, end;
	private boolean[] finish;
	private int finish_counter = 0;
	private int MPM_time = 0;
	
	private int logistic;
	
	public TenantC(double x, double y, int id) {
		super(x, y, id);
		this.setSuccessors(new ArrayList<List<Integer>>());
		this.setTenants(new ArrayList<TenantS>());
		this.setPredecessors(new HashMap<Integer,List<Integer>>());
		this.logistic = 0;
	}
	
	public void ReadData(String filename) throws IOException {
		DataReader data = new DataReader(filename);
		try {
			this.setNbTenants(data.next());
			this.setNbServices(data.next());
			this.setProcessings(new int[this.getNbTenants()]);
			this.setFinish(new boolean[this.getNbTenants()]);
			for (int i = 0; i < this.getNbServices(); i++) {
				data.next();
			}
			for (int i = 0; i < this.getNbTenants(); i++) {
				int processing = data.next();
				this.getProcessings()[i] = processing;
				TenantS subt = new TenantS(this.getX(), this.getY(), i, this.getId());
				subt.setSuperRelease(this.getRelease());
				subt.setProcessing(processing);
				
				for (int j = 0; j < this.getNbServices(); j++) {
					int amount = data.next();
					if (amount > 0) {
						subt.setServicetype(j);
						}
					}
				
				if (i == 0) {
					subt.setServicetype(-1);
					subt.setRelease(this.getRelease());
					}
				else if (i == this.getNbTenants() - 1){
					subt.setServicetype(-1);	
					subt.setFinal(true);
					}
				
				int nbSuccessors = data.next();
//				int[] successors = new int[nbSuccessors];
				List<Integer> successors = new ArrayList<Integer>();
				for (int j = 0; j < nbSuccessors; j++) {
					int succid = data.next() - 1;
					successors.add(succid);
					if (this.getPredecessors().containsKey(succid)) {
						this.getPredecessors().get(succid).add(i);
					}else {
						this.getPredecessors().put(succid, new ArrayList<Integer>());
						this.getPredecessors().get(succid).add(i);
					}
				}
				this.getSuccessors().add(successors);
				this.addTenant(subt);
				
			}
		this.getPredecessors().put(0, new ArrayList<Integer>());
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}
	}
	
	public int getNbTenants() {
		return nbTenants;
	}

	public void setNbTenants(int nbTenants) {
		this.nbTenants = nbTenants;
	}

	public int getNbServices() {
		return nbServices;
	}

	public void setNbServices(int nbServices) {
		this.nbServices = nbServices;
	}

	public List<TenantS> getTenants() {
		return tenants;
	}

	public void setTenants(List<TenantS> tenants) {
		this.tenants = tenants;
	}
	
	public void addTenant(TenantS tenant) {
		this.getTenants().add(tenant);
	}
	
	public TenantS get(int i) {
		return this.getTenants().get(i);
	}
	
	public int size() {
		return this.getTenants().size();
	}

	public List<List<Integer>> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<List<Integer>> successors) {
		this.successors = successors;
	}

	public Map<Integer, List<Integer>> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(Map<Integer, List<Integer>> predecessors) {
		this.predecessors = predecessors;
	}

	public int[] getProcessings() {
		return processings;
	}

	public void setProcessings(int[] processings) {
		this.processings = processings;
	}

	public boolean[] getFinish() {
		return finish;
	}

	public void setFinish(boolean[] finish) {
		this.finish = finish;
	}
	
	public void finish(int i) {
		this.finish[i] = true;
		this.finish_counter ++ ;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public void reset() {
		for (TenantS t : this.tenants) {
			t.reset();
		}
		for (int i = 0; i < finish.length; i++) {
			this.finish[i] = false;
		}
		this.setEnd(Integer.MAX_VALUE);
		this.setLogistic(0);
	}
	
	public void generateMPM() {
		int[] end = new int[this.getNbTenants()];
		int[] start = new int[this.getNbTenants()];
		Set<Integer> active = new HashSet<Integer>();
		active.add(0);
		while (!active.isEmpty()) {
			for (Integer id : active) {
				start[id] = 0;
				for (Integer pid : this.getPredecessors().get(id)) {
					start[id] = Integer.max(start[id], end[pid]);
				}
				end[id] = start[id] + this.getProcessings()[id];
			}
			Set<Integer> active_copy = new HashSet<Integer>(active);
			for (Integer id : active_copy) {
				for (int sid : this.getSuccessors().get(id)) {
					active.add(sid);
				}
				active.remove(id);
			}
		}
		
		this.setMPM_time(end[this.getNbTenants()-1]);
	}

	@Override
	public String toString() {
		return Arrays.toString(this.getProcessings());
	}

	public int getMPM_time() {
		return MPM_time;
	}

	public void setMPM_time(int mPM_time) {
		MPM_time = mPM_time;
	}

	public int getLogistic() {
		return logistic;
	}

	public void setLogistic(int logistic) {
		this.logistic = logistic;
	}
	
	public void addLogistic(int logistic) {
		this.logistic += logistic;
	}
	
	public boolean isSubFinal() {
		// TODO subfinal
		return false;
	}
	
	public boolean donable(int id) {
		boolean result = true;
		
		for (int pid : this.getPredecessors().get(id)) {
			result &= this.getFinish()[pid];
			if (result == false) {
				break;
			}
		}		
		return result;
	}
	
	public List<TenantS> getPredecessors(int id) {
		List<Integer> ids = this.getPredecessors().get(id);
		List<TenantS> result = new ArrayList<TenantS>();
		for (Integer i : ids) {
			result.add(this.get(i));
		}
		return result;
	}
	
	public List<TenantS> getSuccessors(int id) {
		List<TenantS> result = new ArrayList<TenantS>();
		
		for (int i : this.getSuccessors().get(id)) {
			result.add(this.get(i));
		}
		return result;
	}
}
