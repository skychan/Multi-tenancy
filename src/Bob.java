import java.util.*;
import java.io.*;
public class Bob {

	public static void main(String[] args) throws IOException {
		int width = 20;
		int height = 20;
		int nbService = 10;
		int nbTenant = 10;
		int maxTime = 10;
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
		
		int[] release = gen.generateReleaseTime(nbTenant);
		List<TenantC> tenants = gen.generateTenants(release);
		
		/*
		 * Use priority queue to store the simple "active" tenants
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
		
		// set release and put it into priority queue
		for (TenantC tC : tenants) {
			String filename = files[gen.nextInt(files.length)].getName();
			tC.ReadData(fileprefix + filename);
			TenantS tS = tC.get(0);
			tS.setRelease(tC.getRelease());
//			tS.setStart(-1,tC.getRelease());
			active.add(tS);
		}
		// It's hard to set all the sub tenants' distances now, so set the distances in the marker pass.
		// Start the marker pass
		
		int reward_bench = 0;
		int container;
		PriorityQueue<TenantS> marker_active = new PriorityQueue<>(active);
		while (!marker_active.isEmpty()) {
			TenantS tS = marker_active.poll();
			TenantC tC = tenants.get(tS.getSuperid());
//			// set final marker
//			if (marker_active.isEmpty() && tC.getTenants().indexOf(tS) == tC.getNbTnents() - 1) {
//				tS.setFinal(true);
//			}
			
			if (tS.getProcessing() > 0) {
				// TODO set distances
				tS.setDistance(services.get(tS.getServicetype()));
				
				container = gen.nextInt(services.get(tS.getServicetype()).size()) + 1;
				
				// TODO set init start and end (pre-processing)
				int r = tS.getRelease();
				for (Resource resource : services.get(tS.getServicetype()).getResources()) {
					int id = resource.getId();
					int a = resource.getAvailable();
					tS.setStart(id, Math.max(a, r));
					tS.setEnd(id, 0);
				}
				
				// TODO fill
				Map<Integer, Integer> available = new HashMap<Integer, Integer>(services.get(tS.getServicetype()).getAvailable());
				Map<Integer, Integer> y = tS.fill(available, container);
				
				Map<Integer, Integer> end = tS.update(y, available);
				
				// TODO update
				services.get(tS.getServicetype()).setAvailable(available);
				tS.setEnd(end);
			} else {
				tS.setEnd(-1, tS.getRelease());
			}
//			System.out.println(tS.getEndWhole());
			// TODO mark the tS finish and activate
			int end_whole = tS.getEndWhole();
			tC.finish(tS.getId());
			for (int sid : tC.getSuccessors().get(tS.getId())) { // check successor's predecessors
				boolean c = true;
				for (int pid : tC.getPredecessors().get(sid)) {
					c &= tC.getFinish()[pid];
					if (c == false) {
						break;
					}
				}
				if (c && sid < tC.getNbTnents() - 1) {
					TenantS t = tC.get(sid);
					// set release
					t.setRelease(end_whole);
//					// set start 
//					for (Resource resource : services.get(t.getServicetype()).getResources()) {
//						int id = resource.getId();
//						int a = resource.getAvailable();
//						t.setStart(id, Math.max(a, end));
//					}
					// add to active
					marker_active.add(tC.get(sid));
				}
			}
			// TODO get marker value
			if (marker_active.isEmpty()) {
				reward_bench = tS.getEndWhole();
			}
		}
		
		System.out.println(reward_bench);
//		for (Service s : services) {
//			System.out.println(s.getAvailable());
//		}
//		for (Resource res : services.get(3).getResources()) {
//			System.out.println(res);
//		}
		
//		for (TenantC tC : tenants) {
//			System.out.println(tC);
//		}
			
		System.out.println("没毛病，law tear");
		
	}

}
