import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GeneratorS {

	public static void main(String[] args) {
		int num = 7;
		int width = 200;
		int height = 200;
		Random generator = new Random(8);
		
		List<Resource> resources = new ArrayList<Resource>();
		
		for (int i = 0; i < num; i++) {
			Resource L = new Resource();
			L.setId(i);
			
			L.setX(generator.nextInt(width));
			L.setY(generator.nextInt(height));
			resources.add(L);
		}
		
		for (int i = 0; i < num; i++) {
			System.out.println(resources.get(i));
		}
		
		int nbTenant = 8;
		int maxTime = 100;
		int maxProcessing = 10;
		
		List<TenantS> tenants = new ArrayList<TenantS>();
		for (int i = 0; i < nbTenant; i++) {
			int x = generator.nextInt(width);
			int y = generator.nextInt(height);
			
			TenantS tenant = new TenantS(x,y,resources);
			tenant.setId(i);
			
			tenant.setRelease(generator.nextInt(maxTime));
			tenant.setProcessing(generator.nextInt(maxProcessing));
			
			tenants.add(tenant);
		}
		
		for (int i = 0; i < nbTenant; i++) {
			System.out.println(tenants.get(i));
		}
		
		for (int i = 0; i < 7; i++) {
			System.out.println(tenants.get(0).getDistance(i));
		}
		
	}

}
