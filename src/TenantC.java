import java.io.*;
import java.util.*;

public class TenantC extends Tenant {

	private int nbTnents, nbServices;
	private List<Integer> processing;
	private List<int[]> successors;
	private List<TenantS> tenants;
	private List<Integer> resourceID;
	
	public TenantC(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}
	
	public void ReadData(String filename) throws IOException {
		DataReader data = new DataReader(filename);
		try {
			this.setNbTnents(data.next());
			this.setNbServices(data.next());
			for (int i = 0; i < this.getNbServices(); i++) {
				data.next();
			}
			this.setProcessing(new ArrayList<Integer>());
			this.setSuccessors(new ArrayList<int[]>());
			for (int i = 0; i < this.getNbTnents(); i++) {
				this.getProcessing().add(data.next());
				for (int j = 0; j < this.getNbServices(); j++) {
					int amount = data.next();
					if (amount > 0) {
						
					}
				}
				int nbSuccessors = data.next();
				int[] successors = new int[nbSuccessors];
				for (int j = 0; j < nbSuccessors; j++) {
					successors[j] = data.next();
				}
				this.getSuccessors().add(successors);
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

	public List<Integer> getProcessing() {
		return processing;
	}

	public void setProcessing(List<Integer> processing) {
		this.processing = processing;
	}



	public List<TenantS> getTenants() {
		return tenants;
	}

	public void setTenants(List<TenantS> tenants) {
		this.tenants = tenants;
	}

	public List<int[]> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<int[]> successors) {
		this.successors = successors;
	}
}
