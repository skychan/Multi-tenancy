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
		
		List<TenantS> tenants = gen.generateTenants(nbTenant, resources);
		
		/*
		 * Set up timeline
		 */
		
		/**
		 * II. Filling each resource repeatedly according to tenant timeline.
		 */
		
		
		// Need to find a way to solve the problem of perfect 
	}

}
