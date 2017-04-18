
public class Resource {
	private double x,y;
	private int id;
	private int available;
	private int sid;
	public Resource() {
		// TODO Auto-generated constructor stub
		this.available = 0;
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
	}

}
