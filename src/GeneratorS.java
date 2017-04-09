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
}
