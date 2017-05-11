import java.util.*;

import com.sun.org.glassfish.external.statistics.annotations.Reset;


public class Resource {
	private double x,y;
	private int id;
	private int available;
	private int sid;
	
	private List<Integer> use;
	
	public Resource() {
		// TODO Auto-generated constructor stub
		this.available = 0;
		this.use = new ArrayList<Integer>();
	}


	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Res " + sid + "-" + id + ", [x=" + x + ", y=" + y + "]";
	}

	public int getAvailable() {
		return available;
	}

	public void setAvailable(int available) {
		this.available = available;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}
	
	public void reset() {
		this.available = 0;
		this.use.clear();
	}

	public List<Integer> getUse() {
		return use;
	}

	public void setUse(List<Integer> use) {
		this.use = use;
	}

	public void addUse(Integer value) {
		// TODO Auto-generated method stub
		this.use.add(value);
	}

	public double getTotalUse() {
		// TODO Auto-generated method stub
		return this.use.stream().mapToInt(Integer::intValue).sum();
	}
	
	

}
