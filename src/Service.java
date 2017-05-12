import java.util.*;

public class Service {
	private int id;
	private List<Resource> resources;
	private int capacity;
	private double decay;

	private List<Cell> stateSpace;
	
	public Service(int id) {
		this.setId(id);
		this.setResources(new ArrayList<Resource>());
		this.stateSpace = new ArrayList<Cell>();
		// TODO Auto-generated constructor stub
	}
	
	public void addResource(Resource resource) {
		this.getResources().add(resource);
		resource.setSid(this.getId());
	}
	
//	public void removeResource(Resource resource) {
//		this.getResources().remove(resource);
//	}
	
	public int size() {
		return this.getResources().size();
	}
	
	public Resource get(int i) {
		return this.getResources().get(i);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
	public Map<Integer, Integer> getAvailable() {
		Map<Integer,Integer> available = new HashMap<Integer,Integer>();
		for (Resource res : this.getResources()) {
			available.put(res.getId(),res.getAvailable());
		}
		return available;
	}
	
	public void setAvailable(int id, int a) {
		this.getResources().get(id).setAvailable(a);
	}
	
	public void setAvailable(Map<Integer, Integer> available) {
		for (Resource resource : resources) {
			int id = resource.getId();
			resource.setAvailable(available.get(id));
		}
	}
	
	public void reset() {
		for (Resource resource : this.resources) {
			resource.reset();
		}
	}
	
	public void initState() {
		// init for the service with state space
		Cell originCell = new Cell();
		originCell.setCapacity(this.getCapacity());
		originCell.setDecay(this.getDecay());
		List<Cell> stateCells = new ArrayList<Cell>();
		stateCells.add(originCell);			
		this.setStateSpace(stateCells);
	}
	
	public int getAmount() {
		return this.getResources().size();
	}

	public List<Cell> getStateSpace() {
		return stateSpace;
	}

	public void setStateSpace(List<Cell> stateSpace) {
		this.stateSpace = stateSpace;
	}
	
	public double explore(State state) {
		// Check the next state and get the best value of this state
		double result = 0.0;
		Cell cell = this.getCell(state);
		if (cell != null) {
//			System.out.println(cell.getActionCount());
			result = cell.getActionValue();
		}
		else {
			System.out.println("Shit!!");
		}
		return result;
	}
	
	public Cell getCell(State state) {
		// get the cell by the state
		Cell result = null;
		for (Cell cell : this.getStateSpace()) {
			if (cell.checkState(state)) {
				result = cell;
				break;
			}
		}
		return result;
	}
	
	public void Split(Cell cell) {
		// TODO split the cellSpace
		// first sort by the bound var, record the bound
		int volumn = cell.getCapacity();
		MyEntry<String, Double> rule = cell.getSplitRule();  // get the split feature
		String key = rule.getKey();
		cell.sortByRule(key);
		List<Double> list = cell.getPorperity(key);
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
	
	public void addStateCell(Cell cell) {
		this.stateSpace.add(cell);
	}

	public void addUse(Map<Integer, Integer> y) {
		// TODO Auto-generated method stub
		for (Map.Entry<Integer, Integer> entry : y.entrySet()) {
			this.get(entry.getKey()).addUse(entry.getValue());
		}
		
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public double getDecay() {
		return decay;
	}
	
	public void setDecay(double decay) {
		this.decay = decay;
	}
}
