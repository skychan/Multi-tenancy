import java.util.*;
import java.util.stream.IntStream;


public class TenantS {
	private int id;
	private int x,y;
	private int processing;
	private Map<Integer,Integer> start, end;
	private Integer release;
	private Map<Integer,Integer> distance;
	
//	private Map<Integer, Integer> location;
	
	// sorting the nearest location with constructor?
	public TenantS(int x, int y) {
		this.setX(x);
		this.setY(y);
//		Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
//		int dist = 0;
//		for (Resource resource : resources) {
//			dist = (int) Math.sqrt(Math.pow(this.getX()-resource.getX(),2) + Math.pow(this.getY()-resource.getY(), 2));
//			distances.put(resource.getId(), dist);
//		}
//		
//		this.list = new ArrayList<Map.Entry<Integer,Integer>>(distances.entrySet());
//		
//		Collections.sort(this.list, new Comparator<Map.Entry<Integer, Integer>>() {
//			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2)
//			{
//				return o1.getValue().compareTo(o2.getValue());
//			}
//		});
//		
	}
	
	public List<Integer> getNearest(List<Resource> resources){
		// calculate distances considering the available time
		Map<Integer, Integer> distanceplus = new HashMap<Integer, Integer>();
		Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
		for (Resource resource : resources) {
			int dist = (int) Math.sqrt(Math.pow(this.getX()-resource.getX(),2) + Math.pow(this.getY()-resource.getY(), 2));
			int id = resource.getId();
			distanceplus.put(id, dist + this.getStart().get(id));
			distances.put(id, dist);
		}
				
		List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(distanceplus.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2)
			{
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			ids.add(list.get(i).getKey());
		}
		
		list = new ArrayList<Map.Entry<Integer, Integer>>(distances.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2){
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		
		Map<Integer, Integer> dists = new LinkedHashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> ent : list) {
			dists.put(ent.getKey(), ent.getValue());
		}
		this.setDistance(dists);
//		System.out.println(this.getDistance());
		return ids;
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
	public Integer getRelease() {
		return release;
	}
	public void setRelease(int release) {
		this.release = new Integer(release);
	}
	public int getStartWhole() {
		return Collections.min(start.values());
	}
	public int getEndWhole() {
		return Collections.max(end.values());
	}
	public int getDuration() {
		try {
			return this.getEndWhole() - this.getStartWhole();
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
	
	
	public int[] fill(List<Integer> idResource){
		int n_max = idResource.size();
		int sum_b = 0;
		int[] y = new int[n_max];
		
		for (int id : idResource) {
			sum_b += (this.getStart().get(id) + this.getDistance().get(id));
		}
//		System.out.println(sum_b);

		for (int n = n_max; n > 0; n--) {
			int y_n = this.getProcessing() + sum_b - (this.getStart().get(idResource.get(n-1)) + this.getDistance().get(idResource.get(n-1)))*n;
//			System.out.println(y_n);
			if(y_n >= n){
//				System.out.println(n);
				for (int i = 0; i < n; i++) {
					y[i] = (int) (this.getProcessing() + sum_b - (this.getStart().get(idResource.get(i))+ this.getDistance().get(idResource.get(i)))*n)/n ;
				}
				int residual = this.getProcessing() - IntStream.of(y).sum();
				List<Integer> chosen = idResource.subList(0, n);
//				System.out.println(chosen);
				for (Map.Entry<Integer, Integer> dist : this.getDistance().entrySet()) {
					int id = dist.getKey();
					if(residual >0 && chosen.contains(id)){
						y[idResource.indexOf(id)] += 1;
						residual -= 1;
//						System.out.println(idResource.indexOf(id) + ", " + chosen);
					}					
				}
				
//				System.out.println(residual);

				break;
			}else{
				sum_b -= (this.getStart().get(idResource.get(n-1)) + this.getDistance().get(idResource.get(n-1)));
			}
		}
		return y;
	}

	public Map<Integer, Integer> getStart() {
		return start;
	}

	public void setStart(Map<Integer, Integer> start) {
		this.start = start;
	}

	public Map<Integer, Integer> getEnd() {
		return end;
	}

	public void setEnd(Map<Integer, Integer> end) {
		this.end = end;
	}

	public Map<Integer,Integer> getDistance() {
		return distance;
	}

	public void setDistance(Map<Integer,Integer> distance) {
		this.distance = distance;
	}
	
}
