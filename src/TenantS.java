import java.util.*;
import java.util.stream.IntStream;


public class TenantS extends Tenant {
	
	private int processing;
	private Map<Integer, Integer> start, end;
	private int superid, servicetype;
//	private Map<Integer, Integer> predends;
	private boolean isFinal = false;
	private int logistic;
	
	private Integer superRelease;

	// sorting the nearest location with constructor?
	public TenantS(double x, double y, int id) {
		super(x,y,id);
		this.setStart(new HashMap<Integer, Integer>());
		this.end = new HashMap<Integer, Integer>();
		this.setLogistic(0);
	}
	
	public TenantS(double x, double y, int id, int superid) {
		this(x,y,id);
		this.setSuperid(superid);
	}
	
	public List<Integer> getNearest(Set<Integer> resource_id){
		// calculate distances considering the available time
		Map<Integer, Integer> distanceplus = new HashMap<Integer, Integer>();
//		Map<Integer, Integer> distances = new HashMap<Integer, Integer>();
		for (int id : resource_id) {
//			int dist = (int) Math.sqrt(Math.pow(this.getX()-resource.getX(),2) + Math.pow(this.getY()-resource.getY(), 2));
//			int id = resource.getId();
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
		
		List<Integer> sortedResource = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			sortedResource.add(list.get(i).getKey());
		}
		return sortedResource;
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

	
	
	public Map<Integer, Integer> fill( Map<Integer,Integer> available, int n_max ){
//		System.out.println(available.keySet());
		List<Integer> sortedResource = this.getNearest(available.keySet());
//		System.out.println(service.size());
//		int n_max = idResource.size();
		int sum_b = 0;
		int[] y = new int[available.size()];
		
		for (int i = 0; i< n_max ; i++) {
			int id = sortedResource.get(i);
			sum_b += (this.getStart().get(id) + this.getDistance().get(id));
		}
//		System.out.println(sum_b);

		for (int n = n_max; n > 0; n--) {
			int y_n = this.getProcessing() + sum_b - (this.getStart().get(sortedResource.get(n-1)) + this.getDistance().get(sortedResource.get(n-1)))*n;
//			System.out.println(y_n);
			if(y_n >= n){
//				System.out.println(n);
				for (int i = 0; i < n; i++) {
					y[i] = (int) (this.getProcessing() + sum_b - (this.getStart().get(sortedResource.get(i))+ this.getDistance().get(sortedResource.get(i)))*n)/n ;
				}
				int residual = this.getProcessing() - IntStream.of(y).sum();
				List<Integer> chosen = sortedResource.subList(0, n);
//				System.out.println(chosen);
				for (Map.Entry<Integer, Integer> dist : this.getDistance().entrySet()) {
					int id = dist.getKey();
					if(residual >0 && chosen.contains(id)){
						y[sortedResource.indexOf(id)] += 1;
						residual -= 1;
//						System.out.println(idResource.indexOf(id) + ", " + chosen);
					}
				}
				
//				System.out.println(residual);

				break;
			}else{
				sum_b -= (this.getStart().get(sortedResource.get(n-1)) + this.getDistance().get(sortedResource.get(n-1)));
			}
		}
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (int i = 0; i < y.length; i++) {
			int resId = sortedResource.get(i);
			result.put(resId, y[i]);

		}
//		System.out.println(Arrays.toString(y));
		// this.update(result, service);
		
		return result;		
//		}
		
	}
	
	public Map<Integer, Integer> update(Map<Integer, Integer> allocation, Map<Integer, Integer> available){
		/**
		* 1. resources' available
		* 2. tenant's end
		*/
		Map<Integer, Integer> end = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> d: allocation.entrySet()) {
			int id = d.getKey();
			if (d.getValue() >0) {				
				// int old_a = resources.getAvailable().get(id);
				// resources.setAvailable(id, this.getStart().get(id) + d.getValue());
				available.put(id, this.getStart().get(id) + d.getValue());
				// service.get(id).setAvailable(old_a + d.getValue());
				end.put(id, this.getStart().get(id) + d.getValue() + this.getDistance().get(id));
				// this.setEnd(id, this.getStart().get(id) + d.getValue() + this.getDistance().get(id));
			}else {
				// this.setEnd(id, 0);
				end.put(id, 0);
			}
		}
		
		// this.setEnd(end);
		return end;
	}
	
	public void explore(Map<Integer,Integer> available, int n_max) {
		
	}

	public Map<Integer, Integer> getStart() {
		return start;
	}

	public void setStart(Map<Integer, Integer> start) {
		this.start = start;
	}
	
	public void setStart(int id, int time) {
		this.getStart().put(id, time);
	}

	public Map<Integer, Integer> getEnd() {
		return end;
	}

	public void setEnd(Map<Integer, Integer> end) {
		this.end = end;
		List<Integer> logistic = new ArrayList<Integer>();
		
		for (Map.Entry<Integer, Integer> e : this.end.entrySet()) {
			if (e.getValue() > 0) {
//				this.logistic += this.getDistance().get(e.getKey());
				logistic.add(this.getDistance().get(e.getKey()));
			}
		}
		int log = 0;
		if (logistic.isEmpty() == false) {
			log = Collections.max(logistic);
		}
		
		this.setLogistic(log);
	}
	
	public void setEnd(int id, int end) {
		this.getEnd().put(id, end);
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

	public int getSuperid() {
		return superid;
	}

	public void setSuperid(int superid) {
		this.superid = superid;
	}

	public int getServicetype() {
		return servicetype;
	}

	public void setServicetype(int servicetype) {
		this.servicetype = servicetype;
	}
	
	public void reset() {
		this.start.clear();
		this.end.clear();
		this.setLogistic(0);
	}
	
	@Override
	public String toString() {
		return "Tenant " + superid + "-" + this.getId() + "[" + this.getX() + "," + this.getY() + "] "  + this.getProcessing();
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public int getLogistic() {
		return logistic;
	}

	public void setLogistic(int logistic) {
		this.logistic = logistic;
	}

	public Integer getSuperRelease() {
		return superRelease;
	}

	public void setSuperRelease(Integer superRelease) {
		this.superRelease = superRelease;
	}

}
