import java.util.*;
import java.io.*;

public class GeneratorC extends Generator{

	public GeneratorC(int width, int height) {
		// TODO Auto-generated constructor stub
		super(width,height);
//		DataReader data = new DataReader(filename);
//		int nbTenants, nbServices;
//		try {
//			nbTenants = data.next();
//			nbServices = data.next();
//			
//			
//		} catch (IOException e) {
//			// TODO: handle exception
//			System.err.println("Error: " + e);
//		}
	}
	public GeneratorC(int width, int height, int seed) {
		super(width, height, seed);
	}
	
	public List<Service> generateServices(int num) {
		List<Service> services = new ArrayList<Service>();
		
		for (int i = 0; i < num; i++) {
			int nbResource = generator.nextInt(6) + 1;
			Service S = new Service(i);
			S.setResources(this.generateResources(nbResource));
			services.add(S);
		}		
		return services;
	}

}
