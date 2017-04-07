import java.io.IOException;
import java.util.*;

public class Generator {

	private int width, height;
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

	public List<Resource> generateResources(int num){
		List<Resource> resources = new ArrayList<Resource>();
		for (int i = 0; i < num; i++) {
			Resource L = new Resource();
			L.setId(i);
			L.setX(generator.nextInt(this.getWidth()));
			L.setY(generator.nextInt(this.getHeight()));
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
	
	
	
	
}
