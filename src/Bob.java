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
		 * Generate Complex Tenant
		 */
		File dir = new File(fileprefix);
		File[] files = dir.listFiles();
		List<TenantC> tenants = new ArrayList<TenantC>();
		
		
		int[] release = gen.generateReleaseTime(nbTenant);
		for (int i = 0; i < nbTenant; i++) {
			TenantC t = new TenantC(width, height, i);
			t.setRelease(release[i]);
			tenants.add(t);
		}
		
		/*
		 * Use priority queue to store the simple tenants
		 */
		
		Comparator<TenantS> comparator = new Comparator<TenantS>(){
			public int compare(TenantS o1, TenantS o2){
				int c;
				c = o1.getRelease().compareTo(o2.getRelease());
				if (c == 0) {
					c = tenants.get(o1.getSuperid()).getRelease().compareTo(tenants.get(o2.getSuperid()).getRelease());
				}
				return c;
			}
		};
		
		PriorityQueue<TenantS> active = new PriorityQueue<>(comparator);
		
//		for (TenantC tC: tenants) {
//			String filename;
//			filename = files[rangen.nextInt(files.length)].getName();
//			
//			tC.ReadData(fileprefix + filename);
//			tC.finish(0);
//			int[] succid = tC.getSuccessors().get(0);
//			int releaseTime = tC.getRelease();
//			for (int j : succid) {
//				TenantS tS = tC.getTenants().get(j);
//				tS.setRelease(releaseTime);
//				for (Resource resource : services.get(tS.getServicetype()).getResources()) {
//					tS.setStart(resource.getId(), releaseTime);
//				}
//				
//				active.add(tS);
//			}			
//		}
		
		for (TenantC tC : tenants) {
			String filename = files[rangen.nextInt(files.length)].getName();
			tC.ReadData(fileprefix + filename);
			TenantS tS = tC.get(0);
			tS.setRelease(tC.getRelease());
			tS.setStart(-1,tC.getRelease());
			active.add(tS);
			
			
		}
		
		
		
//		Collections.sort(active, comparator);
		
		int n_max = 3;
		
		System.out.println(active);
		while (!active.isEmpty()) {
			TenantS tS = active.poll();
			TenantC tC = tenants.get(tS.getSuperid());
			// set distances
			tS.setDistance(services.get(tS.getServicetype()));
//			System.out.println(t.getDistance() + "," + t.getServicetype());
			// process
			// fill
//			System.out.println(tS.getProcessing());
			Map<Integer, Integer> y = tS.fill(services.get(tS.getServicetype()), n_max);
//			System.out.println(y);
			int end = tS.getEndWhole();
			tC.finish(tS.getId());
			// and to finish it
			for (int sid : tC.getSuccessors().get(tS.getId())) { // check successor's predecessors
				boolean c = true;
				for (int pid : tC.getPredecessors().get(sid)) {
					c &= tC.getFinish()[pid];
				}
				if (c) {
					TenantS t = tC.get(sid);
					// set release
					t.setRelease(end);
					// set start 
					for (Resource resource : services.get(t.getServicetype()).getResources()) {
						int id = resource.getId();
						int a = resource.getAvailable();
						t.setStart(id, Math.max(a, end));
					}
					// add to active
					active.add(tC.get(sid));
				}
			} 
		}
		
		
		
		System.out.println("没毛病，law tear");
		
	}

}
