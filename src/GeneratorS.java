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
		Vector<Integer> reward = new Vector<>(2);
		int nbResource = resources.getAmount();
		int container;
		int logistic = 0;
		for (int i = 0; i< tenants.size() ; i++) {
			container = this.nextInt(nbResource) + 1;
			TenantS t = tenants.get(i);
			logistic = this.processing(t, resources, logistic, container);
			Statistics s = CalculateState(t.getRelease(), t.getProcessing(), resources.getAvailable(), t.getDistance());
			State state = new State(s.getGap(), container, s.getMean(), s.getSTD(), t.getProcessing());
			if (i == tenants.size() - 1) {
				reward.add(logistic);
				reward.add(t.getEndWhole());
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
	
	public int processing(TenantS t, Service resources, int logistic, int container) {
//		TenantS t = tenants.get(i);
		int r = t.getRelease();
//		int nbResource = resources.getAmount();
		for (Resource resource : resources.getResources()) {
			int id = resource.getId();
			int a = resource.getAvailable();
			t.setStart(id, Math.max(a, r));
			t.setEnd(id, 0);
		}
		
		Map<Integer, Integer> y = t.fill(resources.getAvailable(),container);
		Statistics s = CalculateState(t.getRelease(), t.getProcessing(), resources.getAvailable(), t.getDistance());
		
		for (Map.Entry<Integer, Integer> sol : y.entrySet()) {
			if (sol.getValue() > 0) {
				logistic += t.getDistance().get(sol.getKey());
			}
		}
		
		t.update(y, resources);
		
		return logistic;
	}
	
	public Vector<Integer> explore() {
		Vector<Integer> reward = new Vector<>(2);
		
		return reward;
	}
}
