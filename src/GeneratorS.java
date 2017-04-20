import java.util.*;


public class GeneratorS extends Generator{

	public GeneratorS(int width, int height) {
		super(width, height);
	}
	public GeneratorS(int width, int height, int seed) {
		super(width, height, seed);
	}
	
	public List<TenantS> generateTenants(int[] processing){
		List<TenantS> tenants = new ArrayList<TenantS>();
		for (int i = 0; i < processing.length; i++) {
			int x = generator.nextInt(this.getWidth());
			int y = generator.nextInt(this.getHeight());
			TenantS tenant = new TenantS(x,y,i);
			tenant.setProcessing(processing[i]);
			tenants.add(tenant);
		}
		
		return tenants;
	}
	
	public void onePass(List<TenantS> tenants, Service resources) {
		// reset first
		resources.reset();
		for (TenantS t : tenants) {
			t.reset();
		}
		
//		int reward = 0;
		int nbResource = resources.getAmount();
		int container;
		 // here we need to consider if in the final state or not
		for (int i = 0; i< tenants.size() ; i++) {
			container = this.nextInt(nbResource) + 1; // is the current action
			TenantS t = tenants.get(i);
			
			Map<Integer, Integer> available = new HashMap<Integer, Integer>(resources.getAvailable());
			this.processing(t, resources, container);
			
			/*
			 * Bellman Equation:
			 * Q(s,a) = R(s,a) + \gamma max_{a'\in s'} {Q(s',a')}
			 * 
			 */
			
			Statistics s = CalculateState(t.getRelease(), t.getProcessing(), available, t.getDistance());
			State state = new State(s.getGap(), nbResource, s.getMean(), s.getSTD(), t.getProcessing());
			
			// TODO merge final and not final as follows
			if (t.isFinal()) {
				double Q = this.getBench() - t.getEndWhole();
				state.setReward(container, Q);
			} else {
				for (Cell cell : this.getStateCells()) {
					if (cell.checkState(state)) {
						// TODO calculate R(s,a)
						double R = cell.getReward(container);
						
						// TODO calculate max(next), define the explore func.
						double Q_next = 0;
						Q_next = this.explore(resources, tenants.get(i+1));
						
						// TODO add the new Q to state
						double Q = R + this.getDecay() * Q_next;
						state.setReward(container, Q);
						boolean isFull = cell.addSample(state);
						cell.setReward(container, Q);
						
						if (isFull){
							// TODO split the cellSpace
							// first sort by the bound var, record the bound
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
	
	public Statistics CalculateState(int r, int p, Map<Integer,Integer> a, Map<Integer,Integer> dist) {
		List<Double> resut = new ArrayList<Double>();
		for (Map.Entry<Integer, Integer> av : a.entrySet()) {
			resut.add((double) (Math.max(av.getValue(), r) + dist.get(av.getKey())));
		}
		
		Statistics state = new Statistics(resut, p);
		return state;
	}
	
	public void preprocessing(TenantS t, Service resources) {
		int r = t.getRelease();
		for (Resource resource : resources.getResources()) {
			int id = resource.getId();
			int a = resource.getAvailable();
			t.setStart(id, Math.max(a, r));
			t.setEnd(id, 0);
		}
	}
	
	public void processing(TenantS t, Service resources, int container) {
		this.preprocessing(t, resources);
		Map<Integer, Integer> available = new HashMap<>(resources.getAvailable());
		Map<Integer, Integer> y = t.fill(available,container);		
		
		Map<Integer, Integer> end = t.update(y, available);
		resources.setAvailable(available);
		t.setEnd(end);
	}
	
	public double explore(Service resources, TenantS t) {
		Map<Integer, Double> Q = new HashMap<Integer, Double>();
		int nbResource = resources.getAmount();
		Map<Integer, Integer> availabe = new HashMap<>(resources.getAvailable());
		
		this.preprocessing(t, resources);
		for (int action = 1; action <= nbResource; action++) {
			Map<Integer, Integer> y = t.fill(resources.getAvailable(), action);
			Map<Integer, Integer> end = t.update(y, availabe);
			Statistics s = CalculateState(t.getRelease(), t.getProcessing(), availabe, t.getDistance());
			State state = new State(s.getGap(), nbResource, s.getMean(), s.getSTD(), t.getProcessing());
			if (t.isFinal()) {
				double q = this.getBench() - Collections.max(end.values());
				Q.put(action,q);
			} else {
				for (Cell cell: this.getStateCells()) {
					if (cell.checkState(state)) {
						double q = cell.getReward(action);
						Q.put(action, q);
						break;
					}
				}
			}
		}
		if (Q.isEmpty()) {
			System.out.println(t);
			
		}
		return Collections.max(Q.values());
	}
}
