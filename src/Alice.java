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
		
		
		/*
		 * for RL we need to set the decay for Bellman's EQ
		 */
		double decay = 0.8;
		gen.setDecay(decay);
		
		String fileprefix = "test/";
			
		File dir = new File(fileprefix);
		File[] files = dir.listFiles();
//		List<TenantC> tenants = new ArrayList<TenantC>();
		
		String filename = files[gen.nextInt(files.length)].getName();
		int[] processing = ReadData(fileprefix + filename);
		System.out.println(Arrays.toString(processing));
		
		List<TenantS> tenants = gen.generateTenants(processing);
		tenants.remove(tenants.size()-1);
		int [] rel = gen.generateReleaseTime(tenants.size());
		PriorityQueue<Integer> release = new PriorityQueue<>(new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		for (int i = 0; i < rel.length; i++) {
//			tenants.get(i).setRelease(release[i]);
			release.add(rel[i]);
		}
//		System.out.println(Arrays.toString(rel));
//		System.out.println(release);
		/*
		 * Set up time line, sorting it by release time
		 */
//		Collections.sort(tenants, new Comparator<TenantS>(){
//			public int compare(TenantS o1, TenantS o2){
//				return o1.getRelease().compareTo(o2.getRelease());
//			}
//		});
		
		

		for (TenantS t : tenants) {
			t.setRelease(release.poll());
			t.setDistance(resources);
			System.out.println(t);
			if (tenants.indexOf(t) == tenants.size() -1 ) {
				t.setFinal(true);
			}
		}
		
		/**
		 * II. Filling each resource repeatedly according to tenant time line sequence.
		 * 
		 * Need a copy of available map
		 * 
		 */
		
		/*
		 * Marker pass start
		 * Use the first pass to set as a marker
		 */
		int reward_bench = 0;
		
//		for (int i = 0; i < 20; i++) {
//			System.out.println(gen.onePass(tenants, resources));
//		}

		// int logistic = 0;
		int container;
		for (int i = 0; i< tenants.size() ; i++) {
			TenantS t = tenants.get(i);
			container = gen.nextInt(nbResource) + 1;
			gen.processing(t, resources, container);
			if (i == tenants.size() - 1) {
				reward_bench = t.getEndWhole();
			}
//			System.out.println(t.getEndWhole());
		}
		
		gen.setBench(reward_bench);

		System.out.println(reward_bench);
		
		// End of init pass
		
		// initialize the original cell
		
		/*
		 * Define the cell comparator to sort the list for later new cells
		 */
//		Comparator<Cell> cellComparator = new Comparator<Cell>() {
//			@Override
//			public int compare(Cell o1, Cell o2) {
//				int c;
//				c = Integer.compare(o1.getNum_max(), o2.getNum_max());
//				if (c == 0) {
//					c = Integer.compare(o1.getGap_max(), o2.getGap_max());
//					if (c == 0) {
//						c = Integer.compare(o1.getP_max(), o2.getP_max());
//						if (c == 0) {
//							c = Double.compare(o1.getMean_max(), o2.getMean_max());
//							if (c == 0) {
//								c = Double.compare(o1.getVar_max(), o2.getVar_max());
//							}
//						}
//					}
//				}
//				return c;
//			}
//		};
		
		List<Cell> stateCells = new LinkedList<>(); //cellComparator

		Cell originCell = new Cell();
		originCell.setDecay(decay);
		
		stateCells.add(originCell);
		
		gen.setStateCells(stateCells);
		/**
		 *  The main pass of the presetted tenants
		 */
		
		for (int i = 0; i < 20; i++) {
			gen.onePass(tenants, resources);
//			System.out.println(instances.get(instances.size()-1));
		}
		
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
