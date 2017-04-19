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
		boolean isNotFinal = true;
		 // here we need to consider if in the final state or not
		for (int i = 0; i< tenants.size() ; i++) {
			container = this.nextInt(nbResource) + 1;
			TenantS t = tenants.get(i);
			if (i == tenants.size() - 1) {
				isNotFinal = false;
			}
			
			// explore first
			Map<Integer, Double> Qset = new HashMap<Integer, Double>();
			if (isNotFinal) {
				Qset = this.explore(resources, t, tenants.get(i+1));
				
			}else {
				// TODO
			}
			
			this.processing(t, resources, container);
			Statistics s = CalculateState(t.getRelease(), t.getProcessing(), resources.getAvailable(), t.getDistance());
			State state = new State(s.getGap(), container, s.getMean(), s.getSTD(), t.getProcessing());
			double Q = Collections.max(Qset.values()) + 0.0;
			
			// TODO check if in the state, add to the cell
			// if the amount is full, split it 
			for (Cell cell : this.getStateCells()) {
				if (cell.checkState(state)) {
					double R = cell.getReward();
					Q = R + this.getDecay()*Q;
					state.setReward(Q);
					cell.addSample(state);
					cell.setReward(Q);
					break;
				}
			}
		}
	}
	
	public Statistics CalculateState(int r, int p, Map<Integer,Integer> a, Map<Integer,Integer> dist) {
		List<Integer> resut = new ArrayList<Integer>();
		for (Map.Entry<Integer, Integer> av : a.entrySet()) {
			resut.add(Math.max(av.getValue(), r) + dist.get(av.getKey()));
		}
		
		Statistics state = new Statistics(resut, p);
		return state;
	}
	
	public void processing(TenantS t, Service resources, int container) {
//		TenantS t = tenants.get(i);
		int r = t.getRelease();
		int nbResource = resources.getAmount();
		for (Resource resource : resources.getResources()) {
			int id = resource.getId();
			int a = resource.getAvailable();
			t.setStart(id, Math.max(a, r));
			t.setEnd(id, 0);
		}
		
		Map<Integer, Integer> available = new HashMap<>(resources.getAvailable());
		Map<Integer, Integer> y = t.fill(available,container);		
		
		Map<Integer, Integer> end = t.update(y, available);
		resources.setAvailable(available);
		t.setEnd(end);
	}
	
	public Map<Integer, Double> explore(Service resources, TenantS t_current, TenantS t_next) {
		int r = t_current.getRelease();
		int nbResource = resources.getAmount();
		for (Resource resource : resources.getResources()) {
			int id = resource.getId();
			int a = resource.getAvailable();
			t_current.setStart(id, Math.max(a, r));
			t_current.setEnd(id, 0);
		}
		// start to explore actions, and we only explore before the final state.
		Map<Integer, Integer> availabe_a = new HashMap<>(resources.getAvailable());
		Map<Integer, Double> Q = new HashMap<Integer, Double>();
			
		for (int action = 1; action <= nbResource; action++) {
			Map<Integer, Integer> y_a = t_current.fill(resources.getAvailable(), action);
			Map<Integer, Integer> end_a = t_current.update(y_a, availabe_a);
			Statistics s = CalculateState(t_current.getRelease(), t_current.getProcessing(), resources.getAvailable(), t_current.getDistance());
			State state = new State(s.getGap(), action, s.getMean(), s.getSTD(), t_current.getProcessing());
			if (t_next.isFinal()) {
				double q = this.getBench() - Collections.max(end_a.values());
				Q.put(action,q);
				state.setReward(q);
			} else {
//				state.setReward(0);
//				Q.put(action, 0.0);
				// TODO
				int r_next = t_next.getRelease();
				Map<Integer, Integer> available_next = new HashMap<Integer, Integer>();
				for (int i = 0; i < nbResource; i++) {
					int a_next = t_current.getStart().get(i) + y_a.get(i);
					available_next.put(i, a_next);
					t_next.setStart(i, Math.max(r_next, a_next));
					t_next.setEnd(i , 0);
				}
				Statistics s_next = CalculateState(t_next.getRelease(), t_next.getProcessing(), available_next, t_next.getDistance());
				
				Map<Integer, Double> Q_next = new HashMap<Integer, Double>();
				for (int action_next = 1; action_next <= nbResource ; action_next++) {
					State state_next = new State(s_next.getGap(), action_next, s_next.getMean(), s_next.getSTD(), t_next.getProcessing());
					for (Cell cell: this.getStateCells()) {
						if (cell.checkState(state_next)) {
							Q_next.put(action_next, cell.getReward());
							break;
						}
					}
				}
				double q = Collections.max(Q_next.values());
				state.setReward(q);
				Q.put(action, q);
			}
		}
		return Q;
	}
}
