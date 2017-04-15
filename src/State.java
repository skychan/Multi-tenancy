import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;


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
	
//	private Vector<Integer> reward;
	
	private int gap;
	private int num;
	private int mean;
	private double var;
	private int p;
	
	
	public State(int gap, int num, int mean, double var, int p) {
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


	public int getMean() {
		return mean;
	}


	public void setMean(int mean) {
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


}
