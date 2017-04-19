import java.util.*;


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
	
	private double reward; // (d_makespan,d_logistic)
	
	private int gap_min, gap_max;
	private int num_min, num_max;
	private double mean_min, mean_max;
	private double var_min, var_max;
	private int p_min, p_max;
	
	private PriorityQueue<State> sampleStates;
	
	private int counter = 0;
	
	private double decay;
	
	public Cell() {
		Comparator<State> comparator = new Comparator<State>(){
			public int compare(State o1, State o2){
				int c;
				c = Integer.compare(o1.getNum(), o2.getNum());
				if (c == 0) {
					c = Integer.compare(o1.getGap(), o2.getGap());
					if (c == 0) {
						c = Integer.compare(o1.getP(), o2.getP());
						if (c == 0) {
							c = Double.compare(o1.getMean(), o2.getMean());
							if (c == 0) {
								c = Double.compare(o1.getVar(), o2.getVar());
							}
						}
					}
				}
				return c;
			}
		};
		
		this.setSampleStates(new PriorityQueue<>(comparator));
		
		this.setNum_min(1);
		this.setNum_max(Integer.MAX_VALUE);
		this.setGap_min(Integer.MIN_VALUE);
		this.setGap_max(Integer.MAX_VALUE);
		this.setP_min(1);
		this.setP_max(Integer.MAX_VALUE);
		this.setMean_min(0);
		this.setMean_max(Double.MAX_VALUE);
		this.setVar_min(0);
		this.setVar_max(Double.MAX_VALUE);
		
	}
	
	public Cell(int ni, int nx, int gi, int gx, int pi, int px, int mi, int mx, double vi, double vx) {
//		this.set
	}
	
	public int getGap_min() {
		return gap_min;
	}

	public void setGap_min(int gap_min) {
		this.gap_min = gap_min;
	}

	public int getGap_max() {
		return gap_max;
	}

	public void setGap_max(int gap_max) {
		this.gap_max = gap_max;
	}

	public int getNum_min() {
		return num_min;
	}

	public void setNum_min(int num_min) {
		this.num_min = num_min;
	}

	public int getNum_max() {
		return num_max;
	}

	public void setNum_max(int num_max) {
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

	public int getP_min() {
		return p_min;
	}

	public void setP_min(int p_min) {
		this.p_min = p_min;
	}

	public int getP_max() {
		return p_max;
	}

	public void setP_max(int p_max) {
		this.p_max = p_max;
	}

	public void setSampleStates(PriorityQueue<State> sampleStates) {
		this.sampleStates = sampleStates;
	}
	
	public void addSample(State s) {
		this.sampleStates.add(s);
		this.counter++;
		// if the amount is full of capacity, then split into two sub cells
	}
	
	public State removeSample() {
		this.counter--;
		return this.sampleStates.poll();
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
	
	public double getReward() {
		// decay when new instance comes and add its reward
		return this.reward;
	}

	public void setReward(double reward) {
		this.reward = this.decay*this.reward + reward;
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(double decay) {
		this.decay = decay;
	}
}
