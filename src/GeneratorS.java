import java.util.*;


public class GeneratorS {
	
	private int width, height;
	private Random generator = new Random(8);
	private int maxTime, maxProcessing;
	
	public GeneratorS(int width, int height){
		this.setWidth(width);
		this.setHeight(height);
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

	public List<TenantS> generateTenants(int nbTenant, List<Resource> res){
		List<TenantS> tenants = new ArrayList<TenantS>();
		for (int i = 0; i < nbTenant; i++) {
			int x = generator.nextInt(this.getWidth());
			int y = generator.nextInt(this.getHeight());
			
			TenantS tenant = new TenantS(x,y,res);
			tenant.setId(i);
			
			tenant.setRelease(generator.nextInt(this.getMaxTime()));
			tenant.setProcessing(generator.nextInt(this.getMaxProcessing()));
			
			tenants.add(tenant);
		}
		
		return tenants;
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

	public int getMaxProcessing() {
		return maxProcessing;
	}

	public void setMaxProcessing(int maxProcessing) {
		this.maxProcessing = maxProcessing;
	}

}
