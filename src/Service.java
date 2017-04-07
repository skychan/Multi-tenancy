import java.util.*;

public class Service {
	private int id;
	private List<Resource> resources;
	public Service(int id) {
		this.setId(id);
		this.setResources(new ArrayList<Resource>());
		// TODO Auto-generated constructor stub
	}
	
	public void addResource(Resource resource) {
		this.getResources().add(resource);
		resource.setSid(this.getId());
	}
	
//	public void removeResource(Resource resource) {
//		this.getResources().remove(resource);
//	}
	
	public int size() {
		return this.getResources().size();
	}
	
	public Resource get(int i) {
		return this.getResources().get(i);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

}
