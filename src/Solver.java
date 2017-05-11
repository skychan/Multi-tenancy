import ilog.concert.IloException;

import java.io.*;
import java.util.*;


public class Solver {

	public static void main(String[] args) throws IOException, IloException {
		
		// TODO Here for the preparation, data set, basic parameter...
		int width = 20;
		int height = 20;
		int seed = 8;
		int maxTime = 100;
		int nbService = 10;
		int avg_res = 10;
		int nbTenant = 20;
		
		double alpha = 0.5; // logistic duration weight
		
		/*
		 * The parameters for the learner
		 */
		double gamma = 0.9;
		int pass = 1000;
		int cellCapacity = 50;
		double decay = 0.0;
		int nbCases = 1;
		
		// The data group
		String fileprefix = "test/";

		
		
		
		GeneratorC gen = new GeneratorC(width, height, seed);
		gen.setMaxTime(maxTime);
		gen.setGamma(gamma);
		
		
		List<Service> services = gen.generateServices(nbService, avg_res);
		
		// TODO Here for the RL to initialize
		RLsolver solver_RL = new RLsolver();

		solver_RL.setGen(gen);
		solver_RL.setCellCapacity(cellCapacity);
		solver_RL.setDecay(decay);
		solver_RL.setGamma(gamma);
		solver_RL.setPass(pass);
		solver_RL.setAlpha(alpha);
		solver_RL.setNbTenant(nbTenant);
		solver_RL.setFileprefix(fileprefix);
		solver_RL.setServices(services);
		solver_RL.initService();
		
		solver_RL.train(nbCases);
		
		// TODO Here for the CP to initialize
		CPsolver solver_CP = new CPsolver(0.5);
		solver_CP.setServices(services);
		/*
		 * Compare the two solvers prepare the same tenant list 
		 */
		File dir = new File(fileprefix);
		File[] files = dir.listFiles();
		int[] release = gen.generateReleaseTime(nbTenant);
		List<TenantC> tenants = gen.generateTenants(release);
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
		}
		
		solver_RL.solve(tenants,active);
		System.out.println(solver_RL.getObjValue());
		
		solver_CP.solve(tenants);
		System.out.println(solver_CP.getObjValue());
	}

}
