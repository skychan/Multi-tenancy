import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Alice {

	public static void main(String[] args) throws IOException {
		/***
		 * The training process for simple tenant goes like:
		 * 1. Use different group of data sets
		 * 2. Random pick one file as the training input
		 * 3. Set parameters (v, \gamma, \alpha, p) (width,height,seed), decay
		 * 4. If not read the processing data from the file, we also need to consider the number of the tenants nbTenant
		 */
		
		/*
		 * Here goes the main parameters
		 */
		
		int width = 200;
		int height = 200;
		int seed = 2;
		int nbResource = 10;
//		int nbTenant = 0;
		int maxTime = 100;
		double gamma = 0.9;
		double decay = 0.9;
		int cellCapacity = 50;
		double alpha = 0.5; // logistic duration weight
		int pass = 1500;
		
		
		
		/**
		 * Prepare the output file
		 */
		String outputfile = "Output/1.csv";
		Path outputPath = Paths.get(outputfile);
		List<String> outputData = new ArrayList<String>();
		outputData.add("pass,obj,delay,logistic");	
		/**
		 * 
		 * I. Initialization 
		 * 
		 * 1. All the resource and set a_i = 0
		 * 2. Sort all the resource within tenants by distance
		 * 3. Set up the time line according to the arrival sequence of tenants.
		 */
		
		
		GeneratorS gen = new GeneratorS(width,height,seed);
		
		
		/*
		 * Resource first
		 */
		
		Service resources =  new Service(0);
		resources.setResources(gen.generateResources(nbResource,0));
		
		/*
		 * Tenant follows
		 */
		
		gen.setMaxTime(maxTime);
		
		
		/*
		 * for RL we need to set the decay for Bellman's EQ
		 * and cell capacity
		 */
		
		
		gen.setGamma(gamma);
		
		/*
		 * 1. Read file
		 * 2. set processing
		 * 3. set release
		 * 4. set distances
		 */
		String fileprefix = "test/"; // group number 组别
			
		File dir = new File(fileprefix);
		File[] files = dir.listFiles();
		
		String filename = files[gen.nextInt(files.length)].getName();
		int[] processing = ReadData(fileprefix + filename);

		
		List<TenantS> tenants = gen.generateTenants(processing);
//		for (TenantS tst : tenants) {
//			System.out.print(tst.getProcessing() + ", ");
//		}
		tenants.remove(tenants.size()-1);
		
		
		int [] rel = gen.generateReleaseTime(tenants.size());
		PriorityQueue<Integer> release = new PriorityQueue<>(new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		for (int i = 0; i < rel.length; i++) {
			release.add(rel[i]);
		}
		
		for (TenantS t : tenants) {
			t.setRelease(release.poll());
			t.setDistance(resources);
//			System.out.println(t);
			if (tenants.indexOf(t) == tenants.size() -1 ) {
				t.setFinal(true);
			}
		}
		
		System.out.println(resources.getAvailable());
		
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
		Object obj = new Object(alpha);
		int container;
		for (TenantS t : tenants) {
			container = gen.nextInt(nbResource) + 1;
			gen.processing(t, resources, container);
			double d = t.getEndWhole() - t.getRelease();
			obj.addDelay(d - (t.getProcessing()+0.0)/container );
			obj.addLogistic(t.getLogistic());
		}
		gen.setBench(obj.getValue());

		System.out.println(obj.getValue());
		
		// End of init pass		
		/* Initialize the original cell
		 * 1. capacity
		 * 2. decay
		 * 3. cell space
		 */
		List<Cell> stateCells = new LinkedList<>(); //cellComparator

		Cell originCell = new Cell();
		originCell.setCapacity(cellCapacity);
		originCell.setDecay(decay);
		
		stateCells.add(originCell);
		
		gen.setStateCells(stateCells);
		
		/**
		 *  The main pass of the presetted tenants
		 */
		double minvalue = obj.getValue();
		for (int i = 0; i < pass; i++) {
			gen.onePass(tenants, resources,obj);
//			System.out.println(instances.get(instances.size()-1));
//			if (obj.getValue() < minvalue) {
//				System.out.println(obj.getValue());
//				minvalue = obj.getValue();
//			}
			double re = gen.Solve(tenants, resources, obj);
			outputData.add((i+1) + "," + obj.getValue() + "," + obj.getObjDelay() + "," + obj.getObjLogistic());
		}
		System.out.println(gen.Solve(tenants, resources, obj) );
		System.out.println(stateCells.size());
		
		
		
		/***
		 * The testing process:
		 * 1. from start tenant, check the Q-value, chose the biggest one to guide the schedule.
		 * 2. get the object value for each instances
		 */
		
//		double re = gen.Solve(tenants, resources, obj);
//		System.out.println(re);
		Files.write(outputPath, outputData);
		
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
	
/*	public static Statistics CalculateState(int r, int p, Map<Integer,Integer> a, Map<Integer,Integer> dist) {
		List<Integer> resut = new ArrayList<Integer>();
		for (Map.Entry<Integer, Integer> av : a.entrySet()) {
			resut.add(Math.max(av.getValue(), r) + dist.get(av.getKey()));
		}
		
		Statistics state = new Statistics(resut, p);
		return state;
	}*/
	
	
}
