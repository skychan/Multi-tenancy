import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class RLsolver {
	private GeneratorC gen;
	private double gamma;
	private int pass;
	private double alpha;
	private List<Service> services;
	private String fileprefix;
	private PriorityQueue<TenantS> active;
	private int nbTenant;
	
	private double objValue, objDelay, objLogistic;
	
	private double solve_time, trainnint_time;
	
	public double getTrainnint_time() {
		return trainnint_time;
	}

	public void setTrainnint_time(double trainnint_time) {
		this.trainnint_time = trainnint_time;
	}

	public double getSolve_time() {
		return solve_time;
	}

	public void setSolve_time(double solve_time) {
		this.solve_time = solve_time;
	}

	public RLsolver() {
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
		
		this.active = new PriorityQueue<>(comparator);
	

	}
	

	
	public void train(int nbCases) throws IOException{
		/** train with the number of cases,
		* for each case,
		* 0. Generate the tenant list (Done)
		* 1. Init the active list, (Done)
		* 2. Set the bench as the first pass (Done)
		* 3. Go through the passes (Done)
		*/
		for (Service service : services) {
			service.initState();
		}
		long startTime = System.currentTimeMillis();
//		System.out.println("nbCases=" + nbCases + ", pass=" + this.getPass());
		for (int i = 0; i < nbCases; i++) {
			List<TenantC> tenants = this.genTenants();
			this.initActiveList(tenants);
			this.setBench(tenants);
//			List<String> result = new ArrayList<String>();
//			result.add("pass,obj,delay,logistic");
			for (int j = 0; j < this.getPass(); j++) {
//				System.out.println("nbCases" + nbCases + ", nbPass" + this.getPass() +"case" + i + ", pass" + j);
				this.getGen().onePass(tenants, this.services, this.getAlpha());
//				Objective obj = this.getGen().Masterbation(tenants, getServices(), 0.5);
//				result.add(j + "," + obj.getValue() + "," + obj.getDelay() + "," +obj.getLogistic() );
				
			}
//			Files.write(Paths.get("Output/filter.csv"), result);
			
		}
		long stopTme = System.currentTimeMillis();
		double duration = (stopTme - startTime + 0.0)/1000;
		this.setTrainnint_time(duration);
	}
	
	public List<TenantC> genTenants() {
		int[] release = this.getGen().generateReleaseTime(this.getNbTenant());
		List<TenantC> tenants = this.getGen().generateTenants(release);
		return tenants;
	}
	
	public void initActiveList(List<TenantC> tenants) throws IOException {
		this.active.clear();
		
		File dir = new File(this.getFileprefix());
		File[] files = dir.listFiles();
		for (TenantC tC : tenants) {
			String filename = files[this.getGen().nextInt(files.length)].getName();
			tC.ReadData(this.getFileprefix() + filename);
			tC.generateMPM();
			TenantS tS = tC.get(0);
			tS.setRelease(tC.getRelease());
			tC.finish(0);
			tS.setEnd(-1,tS.getRelease());
			List<Integer> sids = tC.getSuccessors().get(0);
			Collections.shuffle(sids, this.getGen().generator);
			for (int sid : sids) {
				TenantS t = tC.get(sid);
				t.setRelease(tC.getRelease());
				this.active.add(tC.get(sid));
			}
		}
		this.gen.setActive(active);
	}
	
	public void setBench(List<TenantC> tenants) {
		for (Service s : this.services) {
			s.reset();
		}
		int container;
		Objective obj = new Objective(this.getAlpha());
		PriorityQueue<TenantS> marker_active = new PriorityQueue<>(this.active);
		while (!marker_active.isEmpty()) {
			TenantS tS = marker_active.poll();
			TenantC tC = tenants.get(tS.getSuperid());
			
			if(tS.isFinal()){
				tS.setEnd(-1,tS.getRelease());
				double d = tS.getRelease() - tC.getRelease();
				tC.setEnd(tS.getRelease());
				obj.addDelay(d - tC.getMPM_time() + 0.0);
				obj.addLogistic(tC.getLogistic());
			}			
			else {
				// TODO set distances
				tS.setDistance(this.getServices().get(tS.getServicetype()));
				container = this.getGen().nextInt(this.getServices().get(tS.getServicetype()).getAmount()) + 1;
				this.getGen().processing(tS, this.getServices().get(tS.getServicetype()), container);
				tC.addLogistic(tS.getLogistic());
			}
			this.getGen().Finish(marker_active, tC, tS);
		}
		this.gen.setBench(obj.getValue()*5);
	}
	
	public void solve(List<TenantC> tenants, PriorityQueue<TenantS> active){
		// Masturbation work
		// init the list first
		long startTime = System.currentTimeMillis();
		this.gen.setActive(active);
		Objective obj = this.getGen().Masterbation(tenants, this.getServices(), this.getAlpha());
		long stopTime = System.currentTimeMillis();
		double duration = (stopTime - startTime + 0.0)/1000;
		this.setSolve_time(duration);
		this.setObjValue(obj.getValue());
		this.setObjDelay(obj.getObjDelay());
		this.setObjLogistic(obj.getObjLogistic());
	}

	public GeneratorC getGen() {
		return gen;
	}

	public void setGen(GeneratorC gen) {
		this.gen = gen;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public int getPass() {
		return pass;
	}

	public void setPass(int pass) {
		this.pass = pass;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public String getFileprefix() {
		return fileprefix;
	}

	public void setFileprefix(String fileprefix) {
		this.fileprefix = fileprefix;
	}

	public int getNbTenant() {
		return nbTenant;
	}

	public void setNbTenant(int nbTenant) {
		this.nbTenant = nbTenant;
	}

	public double getObjValue() {
		return objValue;
	}

	public void setObjValue(double objValue) {
		this.objValue = objValue;
	}

	public double getObjDelay() {
		return objDelay;
	}

	public void setObjDelay(double objDelay) {
		this.objDelay = objDelay;
	}

	public double getObjLogistic() {
		return objLogistic;
	}

	public void setObjLogistic(double objLogistic) {
		this.objLogistic = objLogistic;
	}
}
