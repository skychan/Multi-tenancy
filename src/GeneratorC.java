import java.util.*;
import java.io.*;

public class GeneratorC extends Generator{

	public GeneratorC(int width, int height) {
		// TODO Auto-generated constructor stub
		super(width,height);
//		DataReader data = new DataReader(filename);
//		int nbTenants, nbServices;
//		try {
//			nbTenants = data.next();
//			nbServices = data.next();
//			
//			
//		} catch (IOException e) {
//			// TODO: handle exception
//			System.err.println("Error: " + e);
//		}
	}
	public GeneratorC(int width, int height, int seed) {
		super(width, height, seed);
	}
	
	public List<Service> generateServices(int num) {
		List<Service> services = new ArrayList<Service>();
		
		for (int i = 0; i < num; i++) {
			int nbResource = generator.nextInt(20) + 1;
			Service S = new Service(i);
			S.setResources(this.generateResources(nbResource,i));
			services.add(S);
			
		}		
		return services;
	}
	
	public List<TenantC> generateTenants(int[] releases) {
		List<TenantC> tenants = new ArrayList<TenantC>();
		for (int i = 0; i < releases.length; i++) {
			double x = generator.nextDouble()*this.getWidth();
			double y = generator.nextDouble()*this.getWidth();
			TenantC tenant = new TenantC(x,y,i);
			tenant.setRelease(releases[i]);
			tenants.add(tenant);
		}
		return tenants;
	}

}
