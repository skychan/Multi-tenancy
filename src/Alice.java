import java.util.*;

public class Alice {

	public static void main(String[] args) {
		/**
		 * 
		 * I. Initialization 
		 * 
		 * 1. All the resource and set a_i = 0
		 * 2. Sort all the resource within tenants by distance
		 * 3. Set up the time line according to the arrival sequence of tenants.
		 */
		int width = 200;
		int height = 200;
		
		GeneratorS gen = new GeneratorS(width,height);
		
		
		/*
		 * Resource first
		 */
		int nbResource = 7;
		List<Resource> resources =  gen.generateResources(nbResource);
		
		/*
		 * Tenant follows
		 */
		int maxTime = 100;
		int maxProcessing = 10;
		int nbTenant = 8;
		gen.setMaxTime(maxTime);
		gen.setMaxProcessing(maxProcessing);
		
		List<TenantS> tenants = gen.generateTenants(nbTenant);
		
		/*
		 * Set up time line, sorting it by release time
		 */
		Collections.sort(tenants, new Comparator<TenantS>(){
			public int compare(TenantS o1, TenantS o2){
				return o1.getRelease().compareTo(o2.getRelease());
			}
		});
		

		
		/**
		 * II. Filling each resource repeatedly according to tenant timeline.
		 */
		int timetick = 0; // start with 0
		for (TenantS t : tenants) {
			int p = t.getProcessing();
			int r = t.getRelease();
		}
		
		
		// Need to find a way to solve the problem of perfect 
	}

}
