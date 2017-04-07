import java.util.*;


public class GeneratorS extends Generator{
	
	public GeneratorS(int width, int height) {
		super(width, height);
	}
	public GeneratorS(int width, int height, int seed) {
		super(width, height, seed);
	}
	
	public int nextInt(int num) {
		return this.generator.nextInt(num);
	}
	
	public List<TenantS> generateTenants(int[] processing){
		List<TenantS> tenants = new ArrayList<TenantS>();
		for (int i = 0; i < processing.length; i++) {
			int x = generator.nextInt(this.getWidth());
			int y = generator.nextInt(this.getHeight());
			
			TenantS tenant = new TenantS(x,y,i);
			tenant.setProcessing(processing[i]);
			tenants.add(tenant);
		}
		
		return tenants;
	}
	//	private int width, height;

//	private int maxTime, maxProcessing;
	
//	public GeneratorS(int width, int height){
//		this.setWidth(width);
//		this.setHeight(height);
//	}
	
//	public int getWidth() {
//		return width;
//	}
//
//	public void setWidth(int width) {
//		this.width = width;
//	}
//
//	public int getHeight() {
//		return height;
//	}
//
//	public void setHeight(int height) {
//		this.height = height;
//	}
//
//	public int getMaxTime() {
//		return maxTime;
//	}
//
//	public void setMaxTime(int maxTime) {
//		this.maxTime = maxTime;
//	}
//
//	public int getMaxProcessing() {
//		return maxProcessing;
//	}
//
//	public void setMaxProcessing(int maxProcessing) {
//		this.maxProcessing = maxProcessing;
//	}

}
