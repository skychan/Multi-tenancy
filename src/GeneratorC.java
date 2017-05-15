import java.util.*;
import java.io.*;

public class GeneratorC extends Generator{
	
	private PriorityQueue<TenantS> active;
	
	public GeneratorC(int width, int height) {
		// TODO Auto-generated constructor stub
		super(width,height);
	}
	public GeneratorC(int width, int height, int seed) {
		super(width, height, seed);
	}
	
	public List<Service> generateServices(int num, int avg_res) {
		List<Service> services = new ArrayList<Service>();
		
		for (int i = 0; i < num; i++) {
			int nbResource = generator.nextInt(avg_res/2 + avg_res) + avg_res/2;
			Service S = new Service(i);
			S.setResources(this.generateResources(nbResource,i));
			services.add(S);
		}		
		return services;
	}
	
	public List<TenantC> generateTenants(int[] releases) {
		List<TenantC> tenants = new ArrayList<TenantC>();
		for (int i = 0; i < releases.length; i++) {
			double x = generator.nextDouble()*this.getWidth();
			double y = generator.nextDouble()*this.getWidth();
			TenantC tenant = new TenantC(x,y,i);
			tenant.setRelease(releases[i]);
			tenants.add(tenant);
		}
		return tenants;
	}
	
	public void onePass(List<TenantC> tenants, List<Service> services, double alpha) {
		// TODO
		// reset first
		for (Service service : services) {
			service.reset();
		}
		for (TenantC tenant : tenants) {
			tenant.reset();
		}
		
		Objective obj = new Objective(alpha);
//		System.out.println(passObj.getValue());
		int container = 0;
		State state = new State();
		PriorityQueue<TenantS> active_pass = new PriorityQueue<TenantS>(this.getActive());
		Service service = new Service(-1);
		while (!active_pass.isEmpty()) {
			TenantS tS = active_pass.poll();
			TenantC tC = tenants.get(tS.getSuperid());
			
			if (tS.isFinal()) {
				tS.setEnd(-1, tS.getRelease());
				double d = tS.getRelease() - tC.getRelease();
				obj.addDelay(d - tC.getMPM_time() +0.0);
				obj.addLogistic(tC.getLogistic());
//				service = null;
			}
			else{
				service = services.get(tS.getServicetype());
				int nbResource = service.getAmount();
//				tS.setDistance(service);
				container = this.nextInt(nbResource) + 1;
				
				Map<Integer, Integer> available = new HashMap<Integer, Integer>(service.getAvailable());
				this.processing(tS, service, container);
				// TODO add processing need to add logistic
				tC.addLogistic(tS.getLogistic());
				
				Statistics s = CalculateState(tS.getRelease(), tS.getProcessing(), available, tS.getDistance());
				state = new State(s.getGap(), nbResource, s.getMean(), s.getSTD(), tS.getProcessing());
			}
			
			this.Finish(active_pass, tC, tS); // after finish, the active list updated
			
			// TODO merge the final and subfinal
			boolean leave = true;
//			Service service_next;
			if (active_pass.isEmpty()) {
				double R = this.getBench() - obj.getValue();
				state.setQvalue(container, R);
				leave = false;
				}
			else {
				TenantS tS_next = this.nextS(active_pass);
				if (tS_next!=null) {
					
					Service service_next = services.get(tS_next.getServicetype());
					Statistics s_next = CalculateState(tS_next.getRelease(), tS_next.getProcessing(), service_next.getAvailable(), tS_next.getDistance());
					State state_next = new State(s_next.getGap(), service_next.getAmount(), s_next.getMean(), s_next.getSTD(), tS_next.getProcessing());
					double R = 0;
					double Q_next = service_next.explore(state_next);
					double Q = R + this.getGamma() * Q_next;
					state.setQvalue(container, Q);
					leave = false;
					}
				}
			if (leave == false) {
//				System.out.println(service);
				Cell cell = service.getCell(state);
				if (cell != null) {
					boolean isFull = cell.addSample(state);
					cell.setQvalue(container, state.getQvalue(container));
					if (isFull) {
						service.Split(cell);
						}
					}
				else {
					System.out.println("Fuck!");
					}
				}
			}
		}
	
	
	public Objective Masterbation(List<TenantC> tenants, List<Service> services, double alpha){
		for (Service service : services) {
			service.reset();
		}
		for (TenantC tenant : tenants) {
			tenant.reset();
		}
//		System.out.println(obj.getDelay());
		Objective obj = new Objective(alpha);
//		System.out.println(obj.getDelay());
		Integer container = null;
//		System.out.println(tenants.size());
		while (!active.isEmpty()) {
			TenantS tS = active.poll();
			TenantC tC = tenants.get(tS.getSuperid());
			State state;
			
			if(tS.isFinal()){
//				System.out.println(tS);
				tS.setEnd(-1, tS.getRelease());
				double d = tS.getRelease() - tC.getRelease();
				obj.addDelay(d - tC.getMPM_time() +0.0);
				obj.addLogistic(tC.getLogistic());

			}
			else {
				Service service = services.get(tS.getServicetype());
				int nbResource = service.getAmount();
				tS.setDistance(service);
				Statistics s = CalculateState(tS.getRelease(), tS.getProcessing(), service.getAvailable(), tS.getDistance());
				state = new State(s.getGap(), nbResource, s.getMean(), s.getSTD(), tS.getProcessing());
				
				Cell cell = service.getCell(state);
				container = cell.getAction();
//				container = 1;
//				container = service.getAmount();
				this.processing(tS, service, container);
				// processing need to add logistic
				tC.addLogistic(tS.getLogistic());
			}
			
			this.Finish(active, tC, tS);
			
		}

		return obj;
	}

	public PriorityQueue<TenantS> getActive() {
		return active;
	}
	public void setActive(PriorityQueue<TenantS> active) {
		this.active = new PriorityQueue<TenantS>(active);
	}
	
	public void Finish(PriorityQueue<TenantS> active, TenantC tC, TenantS tS) {
//		int reward_bench = 0;
		int end_whole = tS.getEndWhole();
		tC.finish(tS.getId());
		
		List<Integer> sids = new ArrayList<Integer>();
		for (int sid : tC.getSuccessors().get(tS.getId())) {
			sids.add(sid);
		}
		
		Collections.shuffle(sids, generator);
		for (int sid : sids) { // check successor's predecessors
			if (tC.donable(sid)) {
				TenantS t = tC.get(sid);
				// set release
				t.setRelease(end_whole);
				// add to active
				active.add(tC.get(sid));
			}
		}
	}
	
	public boolean isFinal(PriorityQueue<TenantS> active, TenantS tS, List<TenantC> tenants) {
		boolean isSubFinal = true;
		if (!active.isEmpty() && tS.getProcessing() > 0) {
			PriorityQueue<TenantS> active_explore = new PriorityQueue<>(active);
//			System.out.println(active_explore);
			
			while (!active_explore.isEmpty()) {
				TenantS tx = active_explore.poll();
				if (tx.getId() < tenants.get(tx.getSuperid()).getNbTenants()-1) {
					isSubFinal = false;
					active_explore.add(tx);
					break;
					}
				}
			} else {
				isSubFinal = false;
			}
		return isSubFinal;
	}

	public TenantS nextS(PriorityQueue<TenantS> active){
		PriorityQueue<TenantS> nextActive = new PriorityQueue<>(active);
		TenantS result = null;
		while (!nextActive.isEmpty()) {
			result = nextActive.poll();
			if (result.isFinal()) {
				result = null;
			} else {
				break;
			}
		}
		return result;
	} 
}
