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
	
	private int gap;
	private int num;
	private double mean;
	private double var;
	private int p;
	
	private int reward;
	private boolean end = false;
	
	public State(int gap, int num, double mean, double var, int p) {
		this.setGap(gap);
		this.setNum(num);
		this.setMean(mean);
		this.setVar(var);
		this.setP(p);
	}


	public int getGap() {
		return gap;
	}


	public void setGap(int gap) {
		this.gap = gap;
	}


	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}


	public double getMean() {
		return mean;
	}


	public void setMean(double mean) {
		this.mean = mean;
	}


	public double getVar() {
		return var;
	}


	public void setVar(double var) {
		this.var = var;
	}


	public int getP() {
		return p;
	}


	public void setP(int p) {
		this.p = p;
	}


	public int getReward() {
		return reward;
	}


	public void setReward(int reward) {
		this.reward = reward;
	}


	public boolean isEnd() {
		return end;
	}


	public void setEnd(boolean end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "Reward: " + this.reward;
	}
}
