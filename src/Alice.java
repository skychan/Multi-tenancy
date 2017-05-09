import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Alice {
	
	public static GeneratorS gen;

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
		
		int width = 10;
		int height = 10;
		int seed = 2;
		int nbResource;
//		int nbTenant = 0;
		int maxTime = 150;
		double gamma = 0.9;
		double decay = 0.1;
		int cellCapacity = 80;
		double alpha; // logistic duration weight
		int pass = 1500;
		
		double eps = 5;
		
		
		
		/**
		 * Prepare the output file
		 */
		
		
		List<String> outputData_raw = new ArrayList<String>();
		List<String> outputData_filter = new ArrayList<String>();

		String headString = "pass,obj,delay,logistic"; 
		outputData_raw.add(headString);	
		outputData_filter.add(headString);

		/**
		 * 
		 * I. Initialization 
		 * 
		 * 1. All the resource and set a_i = 0
		 * 2. Sort all the resource within tenants by distance
		 * 3. Set up the time line according to the arrival sequence of tenants.
		 */
		
		
		gen = new GeneratorS(width,height,seed);
		
		
		/*
		 * Resource first
		 */
		
		Service resources =  new Service(0);
//		resources.setResources(gen.generateResources(nbResource,0));
		
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
		for (TenantS tst : tenants) {
			System.out.print(tst.getProcessing() + ", ");
		}
		System.out.println();
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

//			System.out.println(t);
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
		
		// TODO:benchmark
//		Object obj = new Object(0.5);
//		int container;
//		for (TenantS t : tenants) {
//			container = gen.nextInt(nbResource) + 1;
//			gen.processing(t, resources, container);
//			double d = t.getEndWhole() - t.getRelease();
//			obj.addDelay(d - (t.getProcessing()+0.0) );
//			obj.addLogistic(t.getLogistic());
//		}
//		gen.setBench(obj.getValue());

//		System.out.println(obj.getValue());
		
		// End of init pass		
		/* Initialize the original cell
		 * 1. capacity
		 * 2. decay
		 * 3. cell space
		 */
		List<Cell> stateCells = new LinkedList<>(); //cellComparator

		Cell originCell = new Cell(eps);
		originCell.setCapacity(cellCapacity);
		originCell.setDecay(decay);
		
		stateCells.add(originCell);
		
		
		gen.setStateCells(stateCells);
		
		/**
		 *  The main pass of the presetted tenants
		 */
//		int nbTenant = tenants.size();
////		double minvalue = obj.getValue();
//		for (int i = 0; i < pass; i++) {
//			gen.onePass(tenants, resources,obj);
//			outputData_raw.add((i+1) + "," + obj.getValue() + "," + obj.getObjDelay()/nbTenant + "," + obj.getObjLogistic()/nbTenant);
//			gen.Solve(tenants, resources, obj);
////			outputData_filter.add((i+1) + "," + obj.getValue() + "," + obj.getObjDelay()/nbTenant + "," + obj.getObjLogistic()/nbTenant);
//		}
////		System.out.println(gen.Masturbation(tenants, resources, obj, null));
//		double re = gen.Solve(tenants, resources, obj);
//		System.out.println("obj of learner " + re);
//		System.out.println(obj.getObjDelay()/nbTenant);
//		System.out.println(obj.getObjLogistic()/nbTenant);
//		System.out.println("obj of nearest " + gen.Masturbation(tenants, resources, obj, 1));
//		System.out.println(obj.getObjDelay()/nbTenant);
//		System.out.println(obj.getObjLogistic()/nbTenant);
//		System.out.println("obj of fair " + gen.Masturbation(tenants, resources, obj, resources.getAmount()));
////		System.out.println(gen.Solve(tenants, resources, obj) );
//		System.out.println(obj.getObjDelay()/nbTenant);
//		System.out.println(obj.getObjLogistic()/nbTenant);
//		System.out.println(tenants.size());
//		
//		System.out.println(stateCells.size());
//		
//		Files.write(Paths.get("Output/raw.csv"), outputData_raw);
//		Files.write(Paths.get("Output/filter.csv"), outputData_filter);
		
		
		
		/**
		 * Experiment on alpha
		 */
//		resources.setResources(gen.generateResources(nbResource,0));
		int denominator = 10;
		int sdenominator = 20;
		
		
		for (int s = 2; s <= sdenominator; s+=2) {
			List<String> outputData_alpha = new ArrayList<String>();
			outputData_alpha.add("alpha,near,fair,elastic");
			nbResource = s;
			resources.setResources(gen.generateResources(nbResource,0));
			
			for (TenantS t : tenants) {
				t.setDistance(resources);
			}
			
			
			for (int a = 0; a <= denominator; a++) {
				alpha = (a+0.0)/denominator;
				Object obj = new Object(alpha);
				
				double obj_near = gen.Masturbation(tenants, resources, obj, 1);
				double obj_fair = gen.Masturbation(tenants, resources, obj, nbResource);
				System.out.println("Start of nbRes=" +s + " alpha=" + alpha);
				double obj_elastic = solve(tenants, resources, cellCapacity, decay, pass, obj);
				outputData_alpha.add(alpha + "," + obj_near + "," + obj_fair + "," + obj_elastic);
				System.out.println("End of nbRes=" +s + "  alpha=" + alpha);
			}
			Files.write(Paths.get("Output/alpha_nbRes_"+ s + ".csv"), outputData_alpha);
			
		}
		
	}
	
	public static double solve(List<TenantS> tenants, Service resources, int cellCapacity, double decay, int pass, Object obj) {
		int nbResource = resources.getAmount();
		obj.clear();
		resources.reset();
		int container;
		for (TenantS t : tenants) {
			t.reset();
			container = gen.nextInt(nbResource) + 1;
			gen.processing(t, resources, container);
			double d = t.getEndWhole() - t.getRelease();
			obj.addDelay(d - (t.getProcessing()+0.0) );
			obj.addLogistic(t.getLogistic());
		}
		gen.setBench(obj.getValue());

		List<Cell> stateCells = new LinkedList<>(); //cellComparator

		Cell originCell = new Cell();
		originCell.setCapacity(cellCapacity);
		originCell.setDecay(decay);
		
		stateCells.add(originCell);
		
		gen.setStateCells(stateCells);

		int nbTenant = tenants.size();
		for (int i = 0; i < pass; i++) {
			gen.onePass(tenants, resources,obj);
		}
		
		return gen.Solve(tenants, resources, obj);
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

	public GeneratorS getGen() {
		return gen;
	}

	public void setGen(GeneratorS gen) {
		this.gen = gen;
	}
	
}
