import java.io.IOException;
import java.util.*;

public class Generator {

	private int width, height;
	private int bench;
	private double decay;
	private List<Cell> stateCells;
//	private Random generator = new Random(8);
	private int maxTime;
	protected Random generator = new Random(8);
	
	public Generator(int width, int height){
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public Generator(int width, int height, int seed) {
		this.setWidth(width);
		this.setHeight(height);
		generator = new Random(seed);
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public List<Resource> generateResources(int num, int sid){
		List<Resource> resources = new ArrayList<Resource>();
		for (int i = 0; i < num; i++) {
			Resource L = new Resource();
			L.setId(i);
			L.setX(generator.nextDouble()*this.getWidth());
			L.setY(generator.nextDouble()*this.getWidth());
			L.setSid(sid);
			resources.add(L);
		}
		return resources;
	}
	
	public int[] generateReleaseTime(int nbTenant) {
		int[] release = new int[nbTenant];
		for (int i = 0; i < nbTenant; i++) {
			release[i] = generator.nextInt(this.getMaxTime());
		}
		return release;
	}
	
	public int nextInt(int num) {
		return this.generator.nextInt(num);
	}
	
	public boolean dominance(Vector<Integer> v1, Vector<Integer> v2) {
		if ((v1.get(0) >= v2.get(0) && v1.get(1) > v2.get(1)) || (v1.get(0) > v2.get(0) && v1.get(1) >= v2.get(1))) {
			return true;
		} else {
			return false;
		}
	}

	public int getBench() {
		return bench;
	}

	public void setBench(int bench) {
		this.bench = bench;
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(double decay) {
		this.decay = decay;
	}

	public List<Cell> getStateCells() {
		return stateCells;
	}

	public void setStateCells(List<Cell> stateCells) {
		this.stateCells = stateCells;
	}
	
}
