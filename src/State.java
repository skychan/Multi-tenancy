import java.util.*;


public class State {
	
	/**
	 * A cell is contains bunch of state that near each other, have:
	 * 1. number of resources
	 * 2. need gap
	 * 3. mean
	 * 4. variant
	 * 5. processing
	 * 6. reward vector
	 */
	
	private double gap;
	private double num;
	private double mean;
	private double var;
	private double p;
	
	private Map<String,Double> porperities;
	
	private Map<Integer, Double> reward; // (action, reward)
	private boolean end = false;
	
	public State(double gap, int num, double mean, double var, int p) {
		this.porperities = new HashMap<String, Double>();
		this.setGap(gap);
		this.setNum(num);
		this.setMean(mean);
		this.setVar(var);
		this.setP(p);
		this.reward = new HashMap<Integer, Double>();
		
		
	}

	public double getPorperity(String key) {
		return this.porperities.get(key);
	}
	
	public double getGap() {
		return gap;
	}


	public void setGap(double gap) {
		this.gap = gap;
		this.porperities.put("gap",this.gap);
	}


	public double getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
		this.porperities.put("num",this.num);
	}


	public double getMean() {
		return mean;
	}


	public void setMean(double mean) {
		this.mean = mean;
		this.porperities.put("mean",this.mean);
	}


	public double getVar() {
		return var;
	}


	public void setVar(double var) {
		this.var = var;
		this.porperities.put("var", this.var);
	}


	public double getP() {
		return p;
	}


	public void setP(double p) {
		this.p = p;
		this.porperities.put("p", this.p);
	}


	public double getReward(int action) {
		return reward.get(action);
	}


	public void setReward(int action, double reward) {
		this.reward.put(action, reward);
	}


	public boolean isEnd() {
		return end;
	}


	public void setEnd(boolean end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "State [porperities=" + porperities + "]";
	}


}
