package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Commuters {
	
	public static void main (String [] args) throws Exception{
		String file = "Pendolarismo.csv";
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line;
		String []header = br.readLine().split(";");
		
		Map<String, Integer>map = new HashMap<String, Integer>();
		
		for(int i = 0; i < header.length; i++) {
			System.out.println(i+" = "+header[i]);
			map.put(header[i].trim(), i);
		}
		
		System.out.println(map.size());
		int treno_cod = map.get("mezzo_cod");
		System.out.println(treno_cod);
		int cont = 0; 
		while((line = br.readLine())!= null) {
			
			if(line.contains("Treno")) {
				String [] r = line.split(";");
				//System.out.println(r[0]);
				for(int i = 0; i < r.length; i++) {
                   // System.out.println("mezzo_cod "+r[10]+" "+r[11]);
				}
				cont++;
			}
		}br.close();
		System.out.println(cont);
		
	}

}
