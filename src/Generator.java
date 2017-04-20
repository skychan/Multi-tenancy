import java.io.IOException;
import java.util.*;

public class Generator {

	private int width, height;
	private int bench;
	private double decay;
	private List<Cell> stateCells;
//	private Random generator = new Random(8);
	private int maxTime;
	protected Random generator = new Random(8);
	
	public Generator(int width, int height){
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public Generator(int width, int height, int seed) {
		this.setWidth(width);
		this.setHeight(height);
		generator = new Random(seed);
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public List<Resource> generateResources(int num, int sid){
		List<Resource> resources = new ArrayList<Resource>();
		for (int i = 0; i < num; i++) {
			Resource L = new Resource();
			L.setId(i);
			L.setX(generator.nextDouble()*this.getWidth());
			L.setY(generator.nextDouble()*this.getWidth());
			L.setSid(sid);
			resources.add(L);
		}
		return resources;
	}
	
	public int[] generateReleaseTime(int nbTenant) {
		int[] release = new int[nbTenant];
		for (int i = 0; i < nbTenant; i++) {
			release[i] = generator.nextInt(this.getMaxTime());
		}
		return release;
	}
	
	public int nextInt(int num) {
		return this.generator.nextInt(num);
	}
	
/*	public boolean dominance(Vector<Integer> v1, Vector<Integer> v2) {
		if ((v1.get(0) >= v2.get(0) && v1.get(1) > v2.get(1)) || (v1.get(0) > v2.get(0) && v1.get(1) >= v2.get(1))) {
			return true;
		} else {
			return false;
		}
	}*/

	public int getBench() {
		return bench;
	}

	public void setBench(int bench) {
		this.bench = bench;
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(double decay) {
		this.decay = decay;
	}

	public List<Cell> getStateCells() {
		return stateCells;
	}
	
	public void addStateCell(Cell cell) {
		this.stateCells.add(cell);
	}

	public void setStateCells(List<Cell> stateCells) {
		this.stateCells = stateCells;
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
		
		// update the resource available and tenant end
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
			
//			if (t.getId() == 7 && t.getSuperid() == 7) {
//				System.out.println(state);
//			}
			
			if (t.isFinal()) {
				double q = this.getBench() - Collections.max(end.values());
				Q.put(action,q);
			} else {
//				if (t.getId() == 7 && t.getSuperid() == 7) {
//					System.out.println(state.getGap());
//					System.out.println(this.getStateCells().size());
//					System.out.println(this.getStateCells().get(0).getPorperity("gap", "min"));
//					System.out.println(this.getStateCells().get(0).getPorperity("gap", "max") >= state.getGap());
//				}
//				
				for (Cell cell: this.getStateCells()) {
//					if (t.getId() == 7 && t.getSuperid() == 7) {
//						String key = "gap";
//						System.out.println(state);
//						if (state.getPorperity(key) >= cell.getPorperity(key, "min") && state.getNum() <= cell.getPorperity(key, "max")) {
//						
//						}else{
//							System.out.println(key);
//						}
//					}
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
//			System.out.println();
			
		}
		return Collections.max(Q.values());
	}
	
}
