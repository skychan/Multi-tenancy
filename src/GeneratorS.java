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
			double x = generator.nextDouble() * this.getWidth();
			double y = generator.nextDouble() * this.getHeight();
			TenantS tenant = new TenantS(x,y,i);
			tenant.setProcessing(processing[i]);
			tenants.add(tenant);
		}
		
		return tenants;
	}
	
	public void onePass(List<TenantS> tenants, Service resources, Object passObj) {
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
			
			double d = t.getEndWhole() - t.getRelease();
			passObj.addDelay(d - t.getProcessing()/container );
			passObj.addLogistic(t.getLogistic());
			
			/*
			 * Bellman Equation:
			 * Q(s,a) = R(s,a) + \gamma max_{a'\in s'} {Q(s',a')}
			 * 
			 */
			
			Statistics s = CalculateState(t.getRelease(), t.getProcessing(), available, t.getDistance());
			State state = new State(s.getGap(), nbResource, s.getMean(), s.getSTD(), t.getProcessing());
			
			// TODO merge final and not final as follows
			if (t.isFinal()) {
				// Terminal state's Q-value = Reward, because no further states !!
				double R = this.getBench() - passObj.getValue();
				state.setQvalue(container, R);
			} else {
				for (Cell cell : this.getStateCells()) {
//					System.out.println(state);
					if (cell.checkState(state)) {
						// TODO calculate R(s,a), since non-terminal state's reward is 0, so no need to calculate it 
//						double R = cell.getReward(container);
						double R = 0;
						
						// TODO calculate max(next), define the explore func.
//						double Q_next = 0;
						double Q_next = this.explore(resources, tenants.get(i+1));
						
						// TODO add the new Q to state
						double Q = R + this.getGamma() * Q_next;
						state.setQvalue(container, Q);
						boolean isFull = cell.addSample(state);
						cell.setQvalue(container, Q);
						
						if (isFull){
							// TODO split the cellSpace
							// first sort by the bound var, record the bound
							int volumn = cell.getCapacity();
							Map.Entry<String, List> rule = cell.getSplitRule();  // get the split feature
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
