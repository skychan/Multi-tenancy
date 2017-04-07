import java.util.*;
import java.io.*;
public class Bob {

	public static void main(String[] args) throws IOException {
		int width = 200;
		int height = 200;
		Random rangen = new Random(2);
		int nbService = 4;
		int nbTenant = 9;
		int maxTime = 100;
		String fileprefix = "data/j30rcp/";
		
		GeneratorC gen = new GeneratorC(width, height, 8);
		gen.setMaxTime(maxTime);
		
		/*
		 * Generate Service
		 */
		List<Service> services = gen.generateServices(nbService);
		/*
		 * Generate Complex Tenant
		 */
		File dir = new File(fileprefix);
		File[] files = dir.listFiles();
//		List<TenantC> tenants = new ArrayList<TenantC>();
		
		int[] release = gen.generateReleaseTime(nbTenant);
		
		for (int i = 0; i < nbTenant; i++) {
			String filename;
			filename = files[rangen.nextInt(files.length)].getName();
			TenantC t = new TenantC(width, height, i);
			t.setRelease(release[i]);
			t.ReadData(fileprefix + filename);
			
			
		}
		
		
		System.out.println("没毛病，law tear");
		
	}

}
