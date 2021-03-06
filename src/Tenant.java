import java.util.*;
import java.util.stream.IntStream;

public class Tenant {
	private int id;
	private double x,y;
	
	private Integer release;
	private Map<Integer,Integer> distance;	
	public Tenant(double x, double y, int id) {
		this.setX(x);
		this.setY(y);
		this.setId(id);
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

	public Map<Integer,Integer> getDistance() {
		return distance;
	}

	public Map setDistance(Service resources) {
		Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
//		 list = new ArrayList<Map.Entry<Integer,Integer>>(distanceplus.entrySet());
		for (Resource resource : resources.getResources()) {
			int dist = (int) Math.sqrt(Math.pow(this.getX()-resource.getX(),2) + Math.pow(this.getY()-resource.getY(), 2));
			int id = resource.getId();
//			distanceplus.put(id, dist + this.getStart().get(id));
			distances.put(id, dist);
		}
		List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(distances.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2){
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		
		Map<Integer, Integer> distance = new LinkedHashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> ent : list) {
			distance.put(ent.getKey(), ent.getValue());
		}
		this.distance = distance;
		
		
		return distance;
	}
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public Integer getRelease() {
		return release;
	}
	public void setRelease(int release) {
		this.release = new Integer(release);
	}

}
