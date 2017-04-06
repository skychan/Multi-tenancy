import java.io.ObjectInputStream.GetField;
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
		 * II. Filling each resource repeatedly according to tenant time line sequence.
		 */
		int container = 5; // determine container numbers
		for (TenantS t : tenants) {
			int p = t.getProcessing();
			int r = t.getRelease();
			
			// determine the starts
			Map<Integer,Integer> start = new HashMap<Integer,Integer>();
			for (Resource resource : resources) {
				int id = resource.getId();
				int a = resource.getAvailable();
//				System.out.print(a+ ",");
				start.put(id,Math.max(a, r));
			}
			t.setStart(start);
			System.out.println(start + ", " + r);
			
			t.setDistance(resources);
			List<Integer> id_resource_candidates = t.getNearest(resources).subList(0, container);
//			System.out.println(id_resource_candidates);
//			System.out.println(Arrays.toString(id_resource_candidates));
			int[] y = t.fill(id_resource_candidates);
			
			System.out.println(Arrays.toString(y) + ", " + t.getProcessing());
			
			// update available and ends
			Map<Integer, Integer> end = new HashMap<Integer, Integer>();
			for (int i = 0; i < y.length; i++) {
				if (y[i] >0) {
					int id = id_resource_candidates.get(i);
					int old_a = resources.get(id).getAvailable();
					resources.get(id).setAvailable(old_a + y[i]);
					
					end.put(id, start.get(id) + y[i] + t.getDistance().get(id));
				}
			}
			
			t.setEnd(end);
			System.out.println(end);
			
		}
		

		// Need to find a way to solve the problem of perfect 
	}

}
