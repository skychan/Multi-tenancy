import java.util.*;


public class TenantS {
	private int id;
	private int x,y;
	private int processing, release;
	private int start, end;
	private List<Map.Entry<Integer,Integer>> list;
	
	public TenantS(int x, int y, List<Resource> resources) {
		this.setX(x);
		this.setY(y);
		Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
		int dist = 0;
		for (Resource resource : resources) {
			dist = (int) Math.sqrt(Math.pow(this.getX()-resource.getX(),2) + Math.pow(this.getY()-resource.getY(), 2));
			distances.put(resource.getId(), dist);
		}
		
		this.list = new ArrayList<Map.Entry<Integer,Integer>>(distances.entrySet());
		
		Collections.sort(this.list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2)
			{
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getProcessing() {
		return processing;
	}
	public void setProcessing(int processing) {
		this.processing = processing;
	}
	public int getRelease() {
		return release;
	}
	public void setRelease(int release) {
		this.release = release;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getDuration() {
		try {
			return this.getEnd() - this.getStart();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return 0;
		}
	}
	@Override
	public String toString() {
		return "Tenant " + id + ", processing=" + processing
				+ ", release=" + release + ", start=" + start + ", end=" + end
				+ ", x=" + x + ", y=" + y + "]";
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public Map.Entry<Integer, Integer> getDistance(int index){
		return this.list.get(index);
	}

}
