import java.util.*;
import java.util.Map.Entry;


public class Cell {
	
	/**
	 * A cell is contains bunch of state that near each other, have:
	 * 1. number of resources
	 * 2. need gap
	 * 3. mean
	 * 4. variant
	 * 5. processing
	 * 6. reward vector
	 */
	
	private Map<Integer, Double> Qvalue; // (action, Q-value)
	
//	private double gap_min, gap_max;
//	private double num_min, num_max;
//	private double mean_min, mean_max;
//	private double var_min, var_max;
//	private double p_min, p_max;
	
	private List<Double> gap;
	private List<Double> num;
	private List<Double> mean;
	private List<Double> var;
	private List<Double> p;
	
	private Map<String, List> porperities;
	private Map<String, Double> bounds;
		
	private List<State> sampleStates;
	
	private int counter = 0;
	
	private double decay;
	
	private int capacity;
	
	public Cell() {
		
		this.sampleStates = new LinkedList<State>();
		
		this.bounds = new HashMap<String, Double>();

		this.bounds.put("gapmin", -Double.MAX_VALUE);
		this.bounds.put("gapmax", Double.MAX_VALUE);
		this.bounds.put("nummin", 0.0);
		this.bounds.put("nummax", Double.MAX_VALUE);
		this.bounds.put("meanmin", 0.0);
		this.bounds.put("meanmax", Double.MAX_VALUE);
		this.bounds.put("varmin", 0.0);
		this.bounds.put("varmax", Double.MAX_VALUE);
		this.bounds.put("pmin", 0.0);
		this.bounds.put("pmax", Double.MAX_VALUE);
		
//			this.setNum_min(1);
//		this.setNum_max(Double.MAX_VALUE);
//		this.setGap_min(Double.MIN_VALUE);
//		this.setGap_max(Double.MAX_VALUE);
//		this.setP_min(1);
//		this.setP_max(Double.MAX_VALUE);
//		this.setMean_min(0);
//		this.setMean_max(Double.MAX_VALUE);
//		this.setVar_min(0);
//		this.setVar_max(Double.MAX_VALUE);
		
		this.Qvalue = new HashMap<Integer, Double>();
		
		this.porperities = new HashMap<String,List>();
		this.gap = new LinkedList<Double>();
		this.num = new LinkedList<Double>();
		this.mean = new LinkedList<Double>();
		this.var = new LinkedList<Double>();
		this.p = new LinkedList<Double>();
		this.porperities.put("gap",this.gap);
		this.porperities.put("num",this.num);
		this.porperities.put("mean",this.mean);
		this.porperities.put("var", this.var);
		this.porperities.put("p", this.p);
	}

	public boolean addSample(State s) {
		this.sampleStates.add(s);
		this.counter++;
		// add the properties to all lists
		for (Map.Entry<String, List> porperity : this.porperities.entrySet()) {
			String key = porperity.getKey();
			porperity.getValue().add(s.getPorperity(key));
//			System.out.println(this.porperities);
		}
		
		// if the amount is full of capacity, then split into two sub cells
		if (this.counter >= this.capacity) {
			return true;
		}else {
			return false;
		}
	}
	
	public State removeSample() {
		this.counter--;
		State s = this.sampleStates.get(counter);
		this.sampleStates.remove(s);
		for (Map.Entry<String, List> porperity : this.porperities.entrySet()) {
			String key = porperity.getKey();
			porperity.getValue().remove(s.getPorperity(key));
		}
		return s;
	}
	
	public int getAmount() {
		return this.counter;
	}
	
	public double getPorperity(String name, String extreme) {
		return this.bounds.get(name+extreme);
	}
	
	public void setPorperity(String name, String extreme, double value) {
		this.bounds.put(name + extreme, value);
	}
	
	public boolean checkState(State state) {
//		state.getNum() >= this.getNum_min() && state.getNum() <= this.getNum_max() &&
//				state.getGap() >= this.getGap_min() && state.getGap() <= this.getGap_max() &&
//				state.getP() >= this.getGap_min() && state.getP() <= this.getP_max() &&
//				state.getMean() >= this.getMean_min() && state.getMean() <= this.getMean_max() &&
//				state.getVar() >= this.getVar_min() && state.getVar() <= this.getVar_max()
		boolean bounded = true;
		for (String porperityName : this.porperities.keySet()) {
			boolean inInterval = state.getPorperity(porperityName) >= this.getPorperity(porperityName, "min") && state.getPorperity(porperityName) <= this.getPorperity(porperityName, "max");
			bounded &= inInterval;
			if (!bounded) {
				break;
			}
		}		
//		if ( bounded ) {
//			return true;
//		} else {
//			return false;
//		}
		return bounded;
	}
	
	public double getQvalue(int action) {
		// decay when new instance comes and add its reward
		if (this.Qvalue.containsKey(action)) {
			return this.Qvalue.get(action);	
		} else {
			return 0;
		}
		
	}
	// TODO:need to change the set Value
	public void setQvalue(int action, double Qvalue) {
		this.Qvalue.put(action, this.decay * this.getQvalue(action) + Qvalue);
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(double decay) {
		this.decay = decay;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public Map.Entry<String, List> getSplitRule() {
		
		PriorityQueue<Map.Entry<String, Double>> std = new PriorityQueue<Map.Entry<String,Double>>(new Comparator<Map.Entry<String, Double> >() {

			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				// TODO Auto-generated method stub
				o2.getValue().compareTo(o1.getValue());
				return 0;
			}
		});
		for (Map.Entry<String, List> porperity : this.porperities.entrySet()) {
			Statistics s = new Statistics(porperity.getValue(), 0);
			Map.Entry<String, Double> item = new MyEntry(porperity.getKey(), s.getSTD());
			
			std.add(item);
		}
		String key = std.peek().getKey();
		Map.Entry<String, List> result = new MyEntry(key, this.porperities.get(key));
		return result;
	}
	
	public void sortByRule(String ruleName) {
		
		Comparator<State> comparator = new Comparator<State>() {
			
			@Override
			public int compare(State o1, State o2) {
				return Double.compare(o1.getPorperity(ruleName), o2.getPorperity(ruleName));
			}
		};
		
		Collections.sort(this.sampleStates, comparator);
	}
	
	public void copy(Cell oldCell) {
		// TODO copy all the old information here
		/*
		 * 1. Reward
		 * 2. bounds (gap,num,mean,var,p)
		 * 3. decay
		 * 4. capacity
		 */
		this.Qvalue = oldCell.getQvalue();
		// bounds
//		this.gap_min = oldCell.getGap_min();
//		this.gap_max = oldCell.getGap_max();
//		this.num_min = oldCell.getNum_min();
//		this.num_max = oldCell.getNum_max();
//		this.mean_min = oldCell.getMean_min();
//		this.mean_max = oldCell.getMean_max();
//		this.var_min = oldCell.getVar_min();
//		this.var_max = oldCell.getVar_max();
//		this.p_min = oldCell.getP_min();
//		this.p_max = oldCell.getP_max();
		for (String key : this.porperities.keySet()) {
			this.setPorperity(key, "min", oldCell.getPorperity(key, "min"));
			this.setPorperity(key, "max", oldCell.getPorperity(key, "max"));
		}
		
		// decay measures the the resistance of previous Q-value
		this.decay = oldCell.getDecay();
		//capacity
		this.capacity = oldCell.getCapacity();
	}

	public Map<Integer, Double> getQvalue() {
		return Qvalue;
	}

	public void setQvalue(Map<Integer, Double> Qvalue) {
		this.Qvalue = Qvalue;
	}
	
	public int getAction() {
		int action = Collections.max(this.getQvalue().entrySet(), Map.Entry.comparingByValue()).getKey();
		return action;
	}

	@Override
	public String toString() {
		return "Cell [bounds=" + bounds + "]";
	}
}
