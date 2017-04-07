import java.io.*;
import java.util.*;

public class TenantC extends Tenant {

	private int nbTnents, nbServices;
	private int[] processings;
	private List<int[]> successors;
	private List<TenantS> tenants;
	private Map<Integer,List<Integer>> predecessors;
//	private List<Integer> services;
//	public TenantC(int id) {
//		super(id);
//	}
	public TenantC(int x, int y, int id) {
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
			for (int i = 0; i < this.getNbServices(); i++) {
				data.next();
			}
			for (int i = 0; i < this.getNbTnents(); i++) {
				this.getProcessings()[i] = data.next();
//				this.getProcessing().add(data.next());
				TenantS subt = new TenantS(this.getX(), this.getY(), i, this.getId());
				subt.setRelease(this.getRelease());
				for (int j = 0; j < this.getNbServices(); j++) {
					int amount = data.next();
					if (amount > 0) {
						subt.setServicetype(j);
					}
				}
				int nbSuccessors = data.next();
				int[] successors = new int[nbSuccessors];
				for (int j = 0; j < nbSuccessors; j++) {
					int succid = data.next();
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
		} catch (IOException e) {
			// TODO: handle exception
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

}
