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
	
	private Map<Integer, Double> reward; // (action, reward)
	
	private double gap_min, gap_max;
	private double num_min, num_max;
	private double mean_min, mean_max;
	private double var_min, var_max;
	private double p_min, p_max;
	
	private List<Double> gap;
	private List<Double> num;
	private List<Double> mean;
	private List<Double> var;
	private List<Double> p;
	
	private Map<String, List> porperities;
		
	private List<State> sampleStates;
	
	private int counter = 0;
	
	private double decay;
	
	private int capacity;
	
	public Cell() {
//		Comparator<State> comparator = new Comparator<State>(){
//			public int compare(State o1, State o2){
//				int c;
//				c = Integer.compare(o1.getNum(), o2.getNum());
//				if (c == 0) {
//					c = Integer.compare(o1.getGap(), o2.getGap());
//					if (c == 0) {
//						c = Integer.compare(o1.getP(), o2.getP());
//						if (c == 0) {
//							c = Double.compare(o1.getMean(), o2.getMean());
//							if (c == 0) {
//								c = Double.compare(o1.getVar(), o2.getVar());
//							}
//						}
//					}
//				}
//				return c;
//			}
//		};
		
		this.sampleStates = new LinkedList<State>();
		
		this.setNum_min(1);
		this.setNum_max(Double.MAX_VALUE);
		this.setGap_min(Double.MIN_VALUE);
		this.setGap_max(Double.MAX_VALUE);
		this.setP_min(1);
		this.setP_max(Double.MAX_VALUE);
		this.setMean_min(0);
		this.setMean_max(Double.MAX_VALUE);
		this.setVar_min(0);
		this.setVar_max(Double.MAX_VALUE);
		
		this.reward = new HashMap<Integer, Double>();
		
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
	
	public Cell(int ni, int nx, int gi, int gx, int pi, int px, int mi, int mx, double vi, double vx) {
//		this.set
	}
	
	public double getGap_min() {
		return gap_min;
	}

	public void setGap_min(double gap_min) {
		this.gap_min = gap_min;
	}

	public double getGap_max() {
		return gap_max;
	}

	public void setGap_max(double gap_max) {
		this.gap_max = gap_max;
	}

	public double getNum_min() {
		return num_min;
	}

	public void setNum_min(double num_min) {
		this.num_min = num_min;
	}

	public double getNum_max() {
		return num_max;
	}

	public void setNum_max(double num_max) {
		this.num_max = num_max;
	}

	public double getMean_min() {
		return mean_min;
	}

	public void setMean_min(int mean_min) {
		this.mean_min = mean_min;
	}

	public double getMean_max() {
		return mean_max;
	}

	public void setMean_max(double mean_max) {
		this.mean_max = mean_max;
	}

	public double getVar_min() {
		return var_min;
	}

	public void setVar_min(double var_min) {
		this.var_min = var_min;
	}

	public double getVar_max() {
		return var_max;
	}

	public void setVar_max(double var_max) {
		this.var_max = var_max;
	}

	public double getP_min() {
		return p_min;
	}

	public void setP_min(double p_min) {
		this.p_min = p_min;
	}

	public double getP_max() {
		return p_max;
	}

	public void setP_max(double p_max) {
		this.p_max = p_max;
	}

	
	public boolean addSample(State s) {
		this.sampleStates.add(s);
		this.counter++;
		// add the properties to all lists
		for (Map.Entry<String, List> porperity : this.porperities.entrySet()) {
			String key = porperity.getKey();
			porperity.getValue().add(s.getPorperity(key));
			System.out.println(this.porperities);
		}
		
		// if the amount is full of capacity, then split into two sub cells
		if (this.counter >= this.capacity) {
			return true;
		}else {
			return false;
		}
	}
	
	public void removeSample(State s) {
		this.counter--;
		this.sampleStates.remove(s);
		for (Map.Entry<String, List> porperity : this.porperities.entrySet()) {
			String key = porperity.getKey();
			porperity.getValue().remove(s.getPorperity(key));
		}
	}
	
	public int getAmount() {
		return this.counter;
	}
	
	public boolean checkState(State state) {
		if (state.getNum() >= this.getNum_min() && state.getNum() <= this.getNum_max() &&
			state.getGap() >= this.getGap_min() && state.getGap() <= this.getGap_max() &&
			state.getP() >= this.getGap_min() && state.getP() <= this.getP_max() &&
			state.getMean() >= this.getMean_min() && state.getMean() <= this.getMean_max() &&
			state.getVar() >= this.getVar_min() && state.getVar() <= this.getVar_max() ) {
			return true;
		} else {
			return false;
		}		
	}
	
	public double getReward(int action) {
		// decay when new instance comes and add its reward
		if (this.reward.containsKey(action)) {
			return this.reward.get(action);	
		} else {
			return 0;
		}
		
	}

	public void setReward(int action, double reward) {
		this.reward.put(action, this.decay * this.getReward(action) + reward);
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
	}
}
