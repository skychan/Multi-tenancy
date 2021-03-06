import java.util.*;

public class Objective {
	private double alpha; // weight of logistic
	private List<Double> delay;
	private List<Integer> logistic;
	// int sum = list.stream().mapToInt(Integer::intValue).sum(); sum up
	public Objective() {
		this.delay = new ArrayList<Double>();
		this.logistic = new ArrayList<Integer>();
	}
	public Objective(double alpha) {
		this();
		this.setAlpha(alpha);
	}
	
	public void addLogistic(int logistic) {
		this.logistic.add(logistic);
	}
	
	public void addDelay(double delay) {
		this.delay.add(delay);
	}
	
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	public List<Double> getDelay() {
		return delay;
	}
	public void setDelay(List<Double> delay) {
		this.delay = delay;
	}
	public List<Integer> getLogistic() {
		return logistic;
	}
	public void setLogistic(List<Integer> logistic) {
		this.logistic = logistic;
	}
	
	public void clear() {
		this.delay.clear();
		this.logistic.clear();
	}
	
	public double getObjDelay() {
		int n = this.delay.size();
		return this.delay.stream().mapToDouble(Double::doubleValue).sum();
	}
	
	public double getObjLogistic() {
		int n = this.delay.size();
		return (this.logistic.stream().mapToInt(Integer::intValue).sum() + 0.0);
	}
	
	public double getValue() {
		// TODO: sum up all the logistic and delay with weight
		double delay_t = this.getObjDelay();
		double logistic_t = this.getObjLogistic();
		int n = this.delay.size();
		return ((1 - this.alpha) * delay_t + this.alpha * logistic_t)/n;
	}
	@Override
	public String toString() {
		return "Object [getObjDelay()=" + getObjDelay() + ", getObjLogistic()="
				+ getObjLogistic() + ", getValue()=" + getValue() + "]";
	}
}
