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
	
	public List<State> onePass(List<TenantS> tenants, Service resources, PriorityQueue stateCells) {
		// reset first
		resources.reset();
		for (TenantS t : tenants) {
			t.reset();
		}
		
		List<State> instances = new ArrayList<State>();
		int reward = 0;
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
			
			if (isNotFinal) {
				this.explore();
			}
			
			this.processing(t, resources, container);
			Statistics s = CalculateState(t.getRelease(), t.getProcessing(), resources.getAvailable(), t.getDistance());
			State state = new State(s.getGap(), container, s.getMean(), s.getSTD(), t.getProcessing());
			if (isNotFinal) {
				reward = t.getEndWhole();
				state.setEnd(true);
				state.setReward(reward);
				// add the state
			}else {
				// add the final-1 state's reward
			}
			instances.add(state);
		}
		return instances;
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
	
	public void explore(Service resources) {
		// start to explore actions, and we only explore before the final state.
		Map<Integer, Integer> availabe_a = new HashMap<>(resources.getAvailable());
		if (isNotFinal) {  // if the next state is the final state, get the reward
			for (int action = 1; action <= nbResource; action++) {
				Map<Integer, Integer> y_a = t.fill(resources.getAvailable(), action);
				Map<Integer, Integer> end_a = t.update(y_a, availabe_a);
				
				Statistics s = CalculateState(t.getRelease(), t.getProcessing(), resources.getAvailable(), t.getDistance());
				State state = new State(s.getGap(), container, s.getMean(), s.getSTD(), t.getProcessing());
				
			}
		} else { // normal state, not to get the reward 

		}
	}
}
