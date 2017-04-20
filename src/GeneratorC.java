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
	
	public List<Service> generateServices(int num) {
		List<Service> services = new ArrayList<Service>();
		
		for (int i = 0; i < num; i++) {
			int nbResource = generator.nextInt(20) + 1;
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
	
	public void onePass(List<TenantC> tenants, List<Service> services) {
		// TODO
		// reset first
		for (Service service : services) {
			service.reset();
		}
		for (TenantC tenant : tenants) {
			for (TenantS t : tenant.getTenants()) {
				t.reset();
			}
		}
		
		int container = 0;
		PriorityQueue<TenantS> active_pass = new PriorityQueue<TenantS>(this.getActive());
		while (!active_pass.isEmpty()) {
			TenantS tS = active_pass.poll();
			TenantC tC = tenants.get(tS.getSuperid());
			
			
			State state;
			
			if (tS.getProcessing() > 0) {
				Service service = services.get(tS.getServicetype());
				int nbResource = service.getAmount();
				tS.setDistance(services.get(tS.getServicetype()));
				container = this.nextInt(nbResource) + 1;
				
				Map<Integer, Integer> available = new HashMap<Integer, Integer>(service.getAvailable());
				this.processing(tS, service, container);
				
				
				/*
				 * Bellman Equation
				 * 
				 */
				
				Statistics s = CalculateState(tS.getRelease(), tS.getProcessing(), available, tS.getDistance());
				state = new State(s.getGap(), nbResource, s.getMean(), s.getSTD(), tS.getProcessing());
				
			} else {
				tS.setEnd(-1, tS.getRelease());
				state = new State();
			}
			this.Finish(active_pass, tC, tS); // after finish, the active list updated
			
			// TODO merge the final and subfinal
			
			if (!active_pass.isEmpty() && tS.getProcessing() > 0) {
				PriorityQueue<TenantS> active_explore = new PriorityQueue<>(active_pass);
//				System.out.println(active_explore);
				boolean isSubFinal = true;
				while (!active_explore.isEmpty()) {
					TenantS tx = active_explore.poll();
					if (tx.getId() < tenants.get(tx.getSuperid()).getNbTnents()-1) {
						isSubFinal = false;
						active_explore.add(tx);
						break;
					}
				}
				if (isSubFinal) {
					double Q = this.getBench() - tS.getEndWhole();
					state.setReward(container, Q);
					}else {
					for (Cell cell : this.getStateCells()) {
//						System.out.println(state);
						if (cell.checkState(state)) {
							double R = cell.getReward(container);
							
							double Q_next = 0;
							// TODO explore
							/*
							 * 1. next resource, next tenantS
							 */
							TenantS tS_next = active_explore.poll();
//							System.out.println(tS_next);
//							System.out.println(tenants.get(tS_next.getSuperid()).getTenants());
							if (tS_next.getProcessing() == 0) {
								int[] succs = tenants.get(tS_next.getSuperid()).getSuccessors().get(tS_next.getId());
								int next_id = this.generator.nextInt(succs.length);
//								System.out.println(tenants.get(tS_next.getSuperid()).getSuccessors().get(0));
								tS_next = tenants.get(tS_next.getSuperid()).get(succs[next_id]);
							}		
//							System.out.println(tS_next);
							Service service_next = services.get(tS_next.getServicetype());
//							System.out.println(tS_next.getServicetype());
//							System.out.println(service_next.getResources());
//							System.out.println(tenants.get(7).getX());
							
							Q_next = this.explore(service_next, tS_next);
							
							double Q = R + this.getDecay() * Q_next;
							state.setReward(container, Q);
							boolean isFull = cell.addSample(state);
							cell.setReward(container, Q);
							
							if (isFull) {
								int volumn = cell.getCapacity();
								Map.Entry<String, List> rule = cell.getSplitRule();
								String key = rule.getKey();
								// then chose the biggest, sort the states
								cell.sortByRule(key);
								List<Double> list = rule.getValue();
								Collections.sort(list);
								double middle_bound = 0.5*(list.get(volumn/2) + list.get(volumn/2 - 1));
								// create a new cell, copy all the old cell's information
								Cell child = new Cell();
								child.copy(cell);
								// modify the bounds, 
								cell.setPorperity(key, "max", middle_bound);
								child.setPorperity(key, "min", middle_bound);
								// migrate samples from old to new 
								for (int j = 0; j < volumn/2; j++) {
									child.addSample(cell.removeSample());
								}
								// add child to the stateCells
								this.addStateCell(child);
							}
							break;
						}
					}
				}
			}
			
		}
	}
	
/*	public void preprocessing() {
		// TODO
	}
	
	public void processing() {
		// TODO
	}*/
	
	/*public double explore(Service service, TenantS t) {
		// TODO
		return 0;
	}*/
	public PriorityQueue<TenantS> getActive() {
		return active;
	}
	public void setActive(PriorityQueue<TenantS> active) {
		this.active = active;
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
//				// set start 
//				for (Resource resource : services.get(t.getServicetype()).getResources()) {
//					int id = resource.getId();
//					int a = resource.getAvailable();
//					t.setStart(id, Math.max(a, end));
//				}
				// add to active
				active.add(tC.get(sid));
			}
		}
	}
	
}
