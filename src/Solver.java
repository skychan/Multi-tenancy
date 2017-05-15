import ilog.concert.IloException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;



public class Solver {

	public static void main(String[] args) throws IOException, IloException {
		
		List<String> cpresult = new ArrayList<String>();
		cpresult.add("group,nbTenant,obj,time");
		for (int nbGroup = 1; nbGroup <= 5; nbGroup++) {
			
			for (int nbTenant = 10; nbTenant <= 50; nbTenant+=10) {
				System.out.println("Group " + nbGroup + ", nbTenant " + nbTenant);
				experiment(nbGroup,nbTenant,cpresult);
				}			
		}
		Files.write(Paths.get("Output/CPresult.csv"), cpresult);
		
//		experiment(3);
//		experiment(3, 50);
		
	}
	
	public static void experiment(int nbGroup, int nbTenant, List<String> cpresult) throws IOException, IloException {
		// TODO Here for the preparation, data set, basic parameter...
		int width = 20;
		int height = 20;
		int seed = 8;
		
		int nbService = 10;
		int avg_res = 10;
//		int nbTenant = 30;
		int maxTime = nbTenant*5;
		
		
		
		double alpha = 0.5; // logistic duration weight
		
		/*
		 * The parameters for the learner
		 */
		double gamma = 0.9;
		int pass = 100; // total pass (subpass * cases)
		int cellCapacity = 50;
		double decay = 0.0;
		int nbCases;
		int nbTrainTenant;
		
		// The data group
		String fileprefix = "data/group" + nbGroup + "/";

		
		
		
		GeneratorC gen = new GeneratorC(width, height, seed);
		gen.setMaxTime(maxTime);
		gen.setGamma(gamma);
		
		
		List<Service> services = gen.generateServices(nbService, avg_res);
		for (Service service : services) {
			service.setCapacity(cellCapacity);
			service.setDecay(decay);
//			System.out.println(service.getAmount());
		}
		// TODO Here for the RL to initialize
		RLsolver solver_RL = new RLsolver();

		solver_RL.setGen(gen);
		solver_RL.setGamma(gamma);
		solver_RL.setAlpha(alpha);
		
//		solver_RL.setNbTenant(nbTenant);
//		solver_RL.train(nbCases);
		
		solver_RL.setFileprefix(fileprefix);
		solver_RL.setServices(services);

		
		
		
		
		
		
		
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
		
		solver_CP.solve(tenants);
//		System.out.println(solver_CP.getObjValue());
//		System.out.println(solver_CP.getObjDelay());
//		System.out.println(solver_CP.getObjLogistic());
//		System.out.println(solver_CP.getSolve_time() + " s");
//		cpresult.add("group,nbTenant,obj,time");
		cpresult.add(nbGroup+"," + nbTenant  + "," + solver_CP.getObjValue() + "," + solver_CP.getSolve_time());
		
		double cp_obj = solver_CP.getObjValue();

//		solver_RL.setPass(pass);
//		
//		solver_RL.setNbTenant(nbTenant); 
//		solver_RL.train(10); // nbCases
//		solver_RL.solve(tenants, active);
//		System.out.println(solver_RL.getObjValue());
//		System.out.println(solver_RL.getObjDelay());
//		System.out.println(solver_CP.getObjLogistic());
//		System.out.println(solver_RL.getTrainnint_time() + " s");
		
//		double cp_obj = 1761;
		int nbt = nbTenant/10;
		List<String> trainTimes = new ArrayList<String>();
		List<String> trainObj = new ArrayList<String>();
		for (nbCases = 1; nbCases <= 10; nbCases++) {
			String caseTime = "";
			String caseObj = "";
			int casepass = pass/(nbCases);
			solver_RL.setPass(casepass);
			
			for (nbTrainTenant = 1; nbTrainTenant <= 10; nbTrainTenant++) {
//				System.out.println("case = " + nbCases + "," + nbTrainTenant*nbt);
				solver_RL.setNbTenant(nbTrainTenant * nbt); 
				solver_RL.train(nbCases);
				solver_RL.solve(tenants, active);
//				System.out.println(solver_RL.getObjValue());
//				System.out.println(solver_RL.getTrainnint_time() + " s");
				caseTime += Double.toString(solver_RL.getTrainnint_time()) + ",";
				caseObj += Double.toString(cp_obj/solver_RL.getObjValue()) + ",";
			}
			trainTimes.add(caseTime);
			trainObj.add(caseObj);
		}
		
		Files.write(Paths.get("Output/group" + nbGroup + "/gradient_time_" + nbt + "0.csv"), trainTimes);
		Files.write(Paths.get("Output/group" + nbGroup + "/gradient_obj_" + nbt + "0.csv"), trainObj);
	
	}

}
