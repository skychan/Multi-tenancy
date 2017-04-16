import java.io.*;
import java.util.*;


public class OnePassC {
	private int width, height;
	
	public OnePassC(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	
	
//	public void pass(String filename) throws IOException {
//		/**
//		 * II. Filling each resource repeatedly according to tenant time line sequence.
//		 */
//		int container; // = 4; // determine container numbers
//		for (TenantS t : tenants) {
//			int r = t.getRelease();
//			// determine the starts			
//			Map<Integer,Integer> start = new HashMap<Integer,Integer>();
//			for (Resource resource : resources.getResources()) {
//				int id = resource.getId();
//				int a = resource.getAvailable();
////				System.out.print(a+ ",");
//				start.put(id,Math.max(a, r));
//			}
//			t.setStart(start);
//			System.out.println(start + ", " + r);
//			
//			t.setDistance(resources);
//			
//			container = gen.nextInt(5) + 1;
//			
//			
////			List<Integer> id_resource_candidates = t.getNearest(resources).subList(0, container);
////			System.out.println(id_resource_candidates);
////			System.out.println(Arrays.toString(id_resource_candidates));
//			Map<Integer, Integer> y = t.fill(resources,container);
//			
//			System.out.println(y.values() + ", " + t.getProcessing());
//
//			System.out.println(t.getEnd());
//			
//		}
//		
//
//		// Need to find a way to solve the problem of perfect 
//		
//		
//		// initialize the original cell
//		List<Cell> stateSpace = new LinkedList<Cell>();
//		
//		/*
//		 * Define the cell comparator to sort the list for later new cells
//		 */
//		Comparator<Cell> cellComparator = new Comparator<Cell>() {
//			@Override
//			public int compare(Cell o1, Cell o2) {
//				// TODO Auto-generated method stub
//				int c;
//				c = Integer.compare(o1.getNum_max(), o2.getNum_max());
//				if (c == 0) {
//					c = Integer.compare(o1.getGap_max(), o2.getGap_max());
//					if (c == 0) {
//						c = Integer.compare(o1.getP_max(), o2.getMean_max());
//						if (c == 0) {
//							c = Integer.compare(o1.getMean_max(), o2.getMean_max());
//							if (c == 0) {
//								c = Double.compare(o1.getVar_max(), o2.getVar_max());
//							}
//						}
//					}
//				}
//				
//				return c;
//			}
//		};
//		
//		/*
//		 * The original cell
//		 * 1. set lower and upper bounds
//		 */
//		Cell originCell = new Cell();
//		
//		stateSpace.add(originCell);
//		
//	}
	
	
	public Vector getReward() {
		Vector v = new Vector(5);
		return v;
	}
	
	
}
