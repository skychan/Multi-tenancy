import java.io.*;
import java.util.*;

public class TenantC extends Tenant {

	private int nbTnents, nbServices;
	private int[] processings;
	private List<int[]> successors;
	private List<TenantS> tenants;
	private Map<Integer,List<Integer>> predecessors;
	private int start, end;
	private boolean[] finish;
	private int finish_counter = 0;
	private int MPM_time = 0;
	
	public TenantC(double x, double y, int id) {
		super(x, y, id);
		this.setSuccessors(new ArrayList<int[]>());
		this.setTenants(new ArrayList<TenantS>());
		this.setPredecessors(new HashMap<Integer,List<Integer>>());
	}
	
	public void ReadData(String filename) throws IOException {
		DataReader data = new DataReader(filename);
		try {
			this.setNbTnents(data.next());
			this.setNbServices(data.next());
			this.setProcessings(new int[this.getNbTnents()]);
			this.setFinish(new boolean[this.getNbTnents()]);
			for (int i = 0; i < this.getNbServices(); i++) {
				data.next();
			}
			for (int i = 0; i < this.getNbTnents(); i++) {
				int processing = data.next();
				this.getProcessings()[i] = processing;
				TenantS subt = new TenantS(this.getX(), this.getY(), i, this.getId());
				subt.setRelease(this.getRelease());
				subt.setSuperRelease(this.getRelease());
				subt.setProcessing(processing);
				if (processing > 0) {
//					List<Integer> amount = new ArrayList<Integer>();
					for (int j = 0; j < this.getNbServices(); j++) {
						int amount = data.next();
						if (amount > 0) {
							subt.setServicetype(j);
						}
					}
//					subt.setServicetype(amount.indexOf((Collections.max(amount))));
				} else {
					for (int j = 0; j < this.getNbServices(); j++) {
						data.next();
//						if (amount > 0) {
//							subt.setServicetype(j);
//						}
					}
					subt.setServicetype(-1);
				}
				
				int nbSuccessors = data.next();
				int[] successors = new int[nbSuccessors];
				for (int j = 0; j < nbSuccessors; j++) {
					int succid = data.next() - 1;
					successors[j] = succid;
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
	
	
	
	public int getNbTnents() {
		return nbTnents;
	}

	public void setNbTnents(int nbTnents) {
		this.nbTnents = nbTnents;
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

	public List<int[]> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<int[]> successors) {
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
		if( this.finish_counter == 1 ) {
//			System.out.println(i);
			this.setStart(this.get(i).getStartWhole());
		} // set start 
		
		if( this.finish_counter == this.finish.length - 2) {
			this.setEnd(this.get(i).getEndWhole());
		} // set end
		
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
	}
	
	public void generateMPM() {
		int[] end = new int[this.getNbTnents()];
		int[] start = new int[this.getNbTnents()];
		Set<Integer> active = new HashSet<Integer>();
//		start[0] = 0;
//		end[0] = 0;
		active.add(0);
		while (!active.isEmpty()) {
			for (Integer id : active) {
				start[id] = 0;
//				System.out.println(Arrays.toString(this.getSuccessors().get(this.getNbTnents()-1)));
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
		
		this.setMPM_time(end[this.getNbTnents()-1]);
	}

//	@Override
//	public String toString() {
//		return "Tenant " + this.getId() + ", release=" + this.getRelease() +  ", start=" + start + ", end=" + end;
//	}
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


}
