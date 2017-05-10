import java.util.*;
import java.util.stream.IntStream;
import java.io.*;
public class Bob {

	public static void main(String[] args) throws IOException {
		int width = 20;
		int height = 20;
		int nbService = 10;
		int nbTenant = 20;
		int maxTime = 10;
		int avg_res = 20;
		double alpha = 0.3;
		String fileprefix = "test/";
		
		GeneratorC gen = new GeneratorC(width, height, 8);
		gen.setMaxTime(maxTime);
		
		/*
		 * Generate Service
		 */
		List<Service> services = gen.generateServices(nbService,avg_res);
				
		/*
		 * for RL we need to set the decay for Bellman's EQ
		 * and cell capacity
		 */
		double gamma = 0.8;
		double decay = 0.0;
		int cellCapacity = 50;
		gen.setGamma(gamma);
		
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
					c = o1.getSuperRelease().compareTo(o2.getSuperRelease());
				}
				return c;
			}
		};
		
		PriorityQueue<TenantS> active = new PriorityQueue<>(comparator);
		


		// set release and put it into priority queue
		for (TenantC tC : tenants) {
			String filename = files[gen.nextInt(files.length)].getName();
			tC.ReadData(fileprefix + filename);
			tC.generateMPM();
			TenantS tS = tC.get(0);
			tS.setRelease(tC.getRelease());
			tC.finish(0);
			tS.setEnd(-1,tS.getRelease());
			List<Integer> sids = tC.getSuccessors().get(0);
			Collections.shuffle(sids, gen.generator);
			for (int sid : sids) {
				TenantS t = tC.get(sid);
				t.setRelease(tC.getRelease());
				active.add(tC.get(sid));
			}
//			System.out.println(filename);
		}
		
		gen.setActive(active);
//		System.out.println(active);
		// It's hard to set all the sub tenants' distances now, so set the distances in the marker pass.
		// Start the marker pass
		
//		int reward_bench = 0;
		
		int container;
		Objective obj = new Objective(alpha);
		PriorityQueue<TenantS> marker_active = new PriorityQueue<>(active);
//		System.out.println(marker_active.size());
//		System.out.println(gen.getActive());
//		System.out.println(active);
//		System.out.println(marker_active);
		while (!marker_active.isEmpty()) {
			TenantS tS = marker_active.poll();
			TenantC tC = tenants.get(tS.getSuperid());
			
			if(tS.isFinal()){
				tS.setEnd(-1,tS.getRelease());
				double d = tS.getRelease() - tC.getRelease();
				obj.addDelay(d - tC.getMPM_time() + 0.0);
				obj.addLogistic(tC.getLogistic());
			}			
			else {
				// TODO set distances
//				System.out.println(tS);
				tS.setDistance(services.get(tS.getServicetype()));
				container = gen.nextInt(services.get(tS.getServicetype()).getAmount()) + 1;
				gen.processing(tS, services.get(tS.getServicetype()), container);
				tC.addLogistic(tS.getLogistic());

			}
			// set finish in gen.Finish()
			gen.Finish(marker_active, tC, tS);
		}
	
		
		gen.setBench(obj.getValue()*2);
		System.out.println(obj);
//		System.out.println(obj.getDelay());
//		System.out.println(obj.getLogistic());
		
//		gen.Masterbation(tenants, services, obj.getAlpha());
//		System.out.println(obj.getDelay());
//		System.out.println(obj.getLogistic());
		
		// End of init pass
		/*
		 * Initialize the original cell
		 * 1. capacity
		 * 2. decay
		 * 3. cell space
		 */
		
		for (Service service : services) {
			Cell originCell = new Cell();
			originCell.setCapacity(cellCapacity);
			originCell.setDecay(decay);
			List<Cell> stateCells = new ArrayList<Cell>();
			stateCells.add(originCell);			
			service.setStateSpace(stateCells);
		}
		
		for (int i = 0; i < 100; i++) {
			gen.onePass(tenants, services, obj.getAlpha());
//			System.out.println(obj.getValue());
		}
//		System.out.println(gen.getActive());
//		System.out.println(obj.getValue());
//		System.out.println(gen.getStateCells().size());
		Objective obj_new = gen.Masterbation(tenants, services, obj.getAlpha()); 
		System.out.println(obj_new);
//		System.out.println(obj.getValue());
//		System.out.println(obj.getDelay());
//		System.out.println(obj.getLogistic());
//		System.out.println("没毛病，law tear");
		
	}

}
