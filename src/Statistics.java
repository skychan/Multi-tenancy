import java.util.*;


public class Statistics {
	private List<Double> data;
	private int p;
	
	public Statistics(List data, int p) {
		this.data = data;
		this.p = p;
	}
	
	public double getMax() {
//		System.out.println(Collections.max(this.data));

		return Collections.max(this.data) + 0.0;
	}
	
	public int getSum() {
		int sum = 0;
		for (Double d : data) {
			sum += d;
		}
		return sum;
	}
	
	public double getMean() {
		return this.getSum()/this.data.size();
	}
	
	public double getSTD() {
		double std = 0;
		int n = this.data.size();
		if (n == 1) {
			return std;
		} else {
			int psum = 0;
			double mean = this.getMean();
			for (Double d : data) {
				psum += Math.pow(d - mean, 2);
			}
			std = Math.sqrt(psum/(n-1));
			
			return std;
		}
		
	}
	
	public double getGap() {
		double gap = 0;
		gap = this.getMax() * this.data.size() - this.getSum() - this.p;
		return gap;
	}
}
