import java.util.*;
import java.util.stream.IntStream;


public class TenantS extends Tenant {
	
	private int processing;
	private Map<Integer,Integer> start, end;

	// sorting the nearest location with constructor?
	public TenantS(int x, int y) {
		super(x,y);
	}
	
	public List<Integer> getNearest(List<Resource> resources){
		// calculate distances considering the available time
		Map<Integer, Integer> distanceplus = new HashMap<Integer, Integer>();
//		Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
		for (Resource resource : resources) {
//			int dist = (int) Math.sqrt(Math.pow(this.getX()-resource.getX(),2) + Math.pow(this.getY()-resource.getY(), 2));
			int id = resource.getId();
			distanceplus.put(id, this.getDistance().get(id) + this.getStart().get(id));
//			distances.put(id, dist);
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
		
//		list = new ArrayList<Map.Entry<Integer, Integer>>(distances.entrySet());
//		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
//			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2){
//				return o1.getValue().compareTo(o2.getValue());
//			}
//		});
//		
//		Map<Integer, Integer> dists = new LinkedHashMap<Integer, Integer>();
//		for (Map.Entry<Integer, Integer> ent : list) {
//			dists.put(ent.getKey(), ent.getValue());
//		}
//		this.setDistance(dists);
//		System.out.println(this.getDistance());
		return ids;
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
	public int getProcessing() {
		return processing;
	}
	public void setProcessing(int processing) {
		this.processing = processing;
	}
	public int getStartWhole() {
		return Collections.min(start.values());
	}
	public int getEndWhole() {
		return Collections.max(end.values());
	}
}
