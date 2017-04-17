import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.*;

public class Alice {

	public static void main(String[] args) throws IOException {
		/**
		 * 
		 * I. Initialization 
		 * 
		 * 1. All the resource and set a_i = 0
		 * 2. Sort all the resource within tenants by distance
		 * 3. Set up the time line according to the arrival sequence of tenants.
		 */
		int width = 5;
		int height = 5;
		int seed = 8;
		
		GeneratorS gen = new GeneratorS(width,height,seed);
		
		
		/*
		 * Resource first
		 */
		int nbResource = 7;
		Service resources =  new Service(0);
		resources.setResources(gen.generateResources(nbResource,0));
		Map<Integer,Integer> resourceAvailable = resources.getAvailable();
		
		/*
		 * Tenant follows
		 */
		int maxTime = 100;
		gen.setMaxTime(maxTime);
		
		
		String fileprefix = "test/";
			
		File dir = new File(fileprefix);
		File[] files = dir.listFiles();
//		List<TenantC> tenants = new ArrayList<TenantC>();
		
		String filename = files[gen.nextInt(files.length)].getName();
		int[] processing = ReadData(fileprefix + filename);
//		System.out.println(Arrays.toString(processing));
		
		List<TenantS> tenants = gen.generateTenants(processing);
		int [] release = gen.generateReleaseTime(tenants.size());
		
		for (int i = 0; i < release.length; i++) {
			tenants.get(i).setRelease(release[i]);
		}
		/*
		 * Set up time line, sorting it by release time
		 */
		Collections.sort(tenants, new Comparator<TenantS>(){
			public int compare(TenantS o1, TenantS o2){
				return o1.getRelease().compareTo(o2.getRelease());
			}
		});
		
		for (TenantS t : tenants) {
			int r = t.getRelease();
			Map<Integer,Integer> start = new HashMap<Integer,Integer>();
			for (Resource resource : resources.getResources()) {
				int id = resource.getId();
				int a = resource.getAvailable();
				start.put(id,Math.max(a, r));
			}
			t.setStart(start);
			t.setDistance(resources);
		}
		
		// generate Tenants OK, use it as the learning template board
		
		
		/**
		 * II. Filling each resource repeatedly according to tenant time line sequence.
		 * 
		 * Need a copy of available map
		 * 
		 */
		
		Map available = new HashMap(resourceAvailable);
		
		int container; // = 4; // determine container numbers
		int logistic = 0;
		for (int i = 0; i< tenants.size() ; i++) {
			TenantS t = tenants.get(i);
			container = gen.nextInt(nbResource) + 1;
//			container = 2;
			// calculate gap
//			System.out.println(t.getDistance());
			
//			List<Integer> id_resource_candidates = t.getNearest(resources).subList(0, container);
//			System.out.println(id_resource_candidates);
//			System.out.println(Arrays.toString(id_resource_candidates));
			Map<Integer, Integer> y = t.fill(resources,container);
			Statistics s = CalculateState(t.getRelease(), t.getProcessing(), available, t.getDistance());
			
			for (Map.Entry<Integer, Integer> sol : y.entrySet()) {
				if (sol.getValue() > 0) {
					logistic += t.getDistance().get(sol.getKey());
				}
			}
			
			Map<Integer, Integer> end = t.update(y, available);

//			System.out.println(end);
			System.out.println(y.values() + ", " + t.getProcessing());
//			System.out.println(available);
//			System.out.println(t.getEnd());
			if (i == tenants.size() - 1) {
				System.out.println(Collections.max(end.values()));
			}
			
		}
		

		// Need to find a way to solve the problem of perfect 
		
		
		// initialize the original cell
		List<Cell> stateSpace = new LinkedList<Cell>();
		
		/*
		 * Define the cell comparator to sort the list for later new cells
		 */
		Comparator<Cell> cellComparator = new Comparator<Cell>() {
			@Override
			public int compare(Cell o1, Cell o2) {
				int c;
				c = Integer.compare(o1.getNum_max(), o2.getNum_max());
				if (c == 0) {
					c = Integer.compare(o1.getGap_max(), o2.getGap_max());
					if (c == 0) {
						c = Integer.compare(o1.getP_max(), o2.getMean_max());
						if (c == 0) {
							c = Integer.compare(o1.getMean_max(), o2.getMean_max());
							if (c == 0) {
								c = Double.compare(o1.getVar_max(), o2.getVar_max());
							}
						}
					}
				}
				return c;
			}
		};
		
		/*
		 * The original cell
		 * 1. set lower and upper bounds
		 */
		Cell originCell = new Cell();
		
		stateSpace.add(originCell);
		
		
		
	}
	
	public static int[] ReadData(String filename) throws IOException {
		DataReader data = new DataReader(filename);
		try {
			int nbTenant = data.next();
			int nbService = data.next();
			int[] processing = new int[nbTenant];
			for (int i = 0; i < nbService; i++) {
				data.next();
			}
			for (int i = 0; i < nbTenant; i++) {
				processing[i] = data.next();
//				this.getProcessing().add(data.next());
				for (int j = 0; j < nbService; j++) {
					data.next();
				}
				int nbSuccessors = data.next();
				for (int j = 0; j < nbSuccessors; j++) {
					data.next();
				}
			}
			return processing;
		} catch (IOException e) {
			// TODO: handle exception
			System.err.println("Error: " + e);
			return null;
		}
	}
	
	public static Statistics CalculateState(int r, int p, Map<Integer,Integer> a, Map<Integer,Integer> dist) {
		List<Integer> resut = new ArrayList<Integer>();
		for (Map.Entry<Integer, Integer> av : a.entrySet()) {
			resut.add(Math.max(av.getValue(), r) + dist.get(av.getKey()));
		}
		
		Statistics state = new Statistics(resut, p);
		return state;
	}
}
