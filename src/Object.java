import java.util.*;
import java.util.concurrent.Delayed;

public class Object {
	private double alpha; // weight of logistic
	private List<Double> delay;
	private List<Integer> logistic;
	// int sum = list.stream().mapToInt(Integer::intValue).sum(); sum up
	public Object() {
		this.delay = new ArrayList<Double>();
		this.logistic = new ArrayList<Integer>();
	}
	public Object(double alpha) {
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
	
	public double getValue() {
		// TODO: sum up all the logistic and delay with weight
		double delay_t = this.delay.stream().mapToDouble(Double::doubleValue).sum();
		int logistic_t = this.logistic.stream().mapToInt(Integer::intValue).sum();
		int n = this.delay.size();
		return ((1 - this.alpha) * delay_t + this.alpha * logistic_t)/n;
	}
}
