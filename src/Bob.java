import java.util.*;
import java.io.*;
public class Bob {

	public static void main(String[] args) throws IOException {
		int width = 200;
		int height = 200;
		Random rangen = new Random(2);
		int nbService = 4;
		int nbTenant = 9;
		int maxTime = 100;
		String fileprefix = "test/";
		
		GeneratorC gen = new GeneratorC(width, height, 8);
		gen.setMaxTime(maxTime);
		
		/*
		 * Generate Service
		 */
		List<Service> services = gen.generateServices(nbService);
		/*
		 * Construct active list
		 */
		List<TenantS> active = new LinkedList<TenantS>();
		/*
		 * Generate Complex Tenant
		 */
		File dir = new File(fileprefix);
		File[] files = dir.listFiles();
//		List<TenantC> tenants = new ArrayList<TenantC>();
		
		int[] release = gen.generateReleaseTime(nbTenant);
		
		for (int i = 0; i < nbTenant; i++) {
			String filename;
			filename = files[rangen.nextInt(files.length)].getName();
			TenantC t = new TenantC(width, height, i);
			t.setRelease(release[i]);
			t.ReadData(fileprefix + filename);
			active.add(t.get(0));
		}
		
		Collections.sort(active, new Comparator<TenantS>(){
			public int compare(TenantS o1, TenantS o2){
				return o1.getRelease().compareTo(o2.getRelease());
			}
		});
		
		
//		System.out.println(active);
		while (!active.isEmpty()) {
			List<TenantS> tempList = new LinkedList<>(active);
			for (TenantS t : tempList) {
				// process
				// fill
				// grab new out and insert right position
				// remove itself from active
			}
			
		}
		
		
		
		System.out.println("没毛病，law tear");
		
	}

}
