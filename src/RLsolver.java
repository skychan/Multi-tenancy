import java.io.*;
import java.util.*;


public class RLsolver {
	private GeneratorC gen;
	private double decay, gamma;
	private int pass, cellCapacity;
	
	private List<Service> services;
	
	private File[] files;
	
	List<Cell> stateCells;
	
	
	public RLsolver() {
		this.setStateCells(new LinkedList<Cell>());	
	}
	
	public void init() {
		Cell originCell = new Cell();
		originCell.setCapacity(this.getCellCapacity());
		originCell.setDecay(this.getDecay());
//		this.stateCells.clear();
		stateCells.add(originCell);
		gen.setStateCells(stateCells);
	}
	
	public void train(){
		for (int p = 0; p < this.getPass(); p++) {
//			gen.onePass(tenants, services);
		}
	}
	
	public void solve(List<TenantC> tenants){
		
	}

	public GeneratorC getGen() {
		return gen;
	}

	public void setGen(GeneratorC gen) {
		this.gen = gen;
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(double decay) {
		this.decay = decay;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public int getPass() {
		return pass;
	}

	public void setPass(int pass) {
		this.pass = pass;
	}

	public int getCellCapacity() {
		return cellCapacity;
	}

	public void setCellCapacity(int cellCapacity) {
		this.cellCapacity = cellCapacity;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

	public List<Cell> getStateCells() {
		return stateCells;
	}

	public void setStateCells(List<Cell> stateCells) {
		this.stateCells = stateCells;
	}
}
