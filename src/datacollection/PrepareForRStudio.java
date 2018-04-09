package datacollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PrepareForRStudio {

	public static void main (String [] args) throws Exception{
		String file_b2r = "dataViaggiaTreno\\B2R"; 
		String file_r2b = "dataViaggiaTreno\\R2B";
		// giorno       treno    ritardo
		TreeMap<String, Map<String, String>>mrRe = getDelaysReggio(file_b2r);
		TreeMap<String, Map<String, String>>mrBo = getDelaysBologna(file_r2b);
		print("ReggioDelays.csv",mrRe );
		printAlt("ReggioDelaysAlt.csv",mrRe );
		
		print("BolognaDelays.csv",mrBo );
		printAlt("BolognaDelaysAlt.csv",mrBo );
		
		//Data 4 graphics ---------------------------------------
		print("C:/Users/alket/Documents/traindelays/ReggioDelays.csv",mrRe );
		printAlt("ReggioDelaysAlt.csv",mrRe );
		
		print("C:/Users/alket/Documents/traindelays/BolognaDelays.csv",mrBo );
		printAlt("BolognaDelaysAlt.csv",mrBo );
	}
	public static TreeMap<String, Map<String, String>> getDelaysReggio(String file ) throws Exception{
		TreeMap<String, Map<String, String>>mr = new TreeMap<String, Map<String, String>>();
		File f = new File(file);
		File [] files = f.listFiles();

		for(int i = 0; i < files.length; i++){

			String filename = files[i].getName().substring(23, 33);

			Map<String, String>mi = mr.get(filename);

			if(mi == null){
				mi = new TreeMap<String, String>();
				mr.put(filename, mi);
			}

			BufferedReader br = new BufferedReader(new FileReader(files[i]));

			String line; 
			br.readLine();
			while((line = br.readLine())!= null){

				String [] r = line.split("\t");
                 System.out.println(r.length);
				String city = r[1];
				String treno = r[0];
				
				String data = r[2];
				String trenodata = treno+"\t"+data;
				String rit = r[4];
				if(city.equals("REGGIO EMILIA")) mi.put(trenodata, rit);

			}br.close();

		}
		return mr;
	}
	public static TreeMap<String, Map<String, String>> getDelaysBologna(String file ) throws Exception{
		TreeMap<String, Map<String, String>>mr = new TreeMap<String, Map<String, String>>();
		File f = new File(file);
		File [] files = f.listFiles();

		for(int i = 0; i < files.length; i++){

			String filename = files[i].getName().substring(23, 33);

			Map<String, String>mi = mr.get(filename);

			if(mi == null){
				mi = new TreeMap<String, String>();
				mr.put(filename, mi);
			}

			BufferedReader br = new BufferedReader(new FileReader(files[i]));

			String line; 
			br.readLine();
			while((line = br.readLine())!= null){

				String [] r = line.split("\t");
				String city = r[1];
				String treno = r[0];
				String data = r[2];
				String trenodata = treno+"\t"+data;

				String rit = r[4];
				if(city.equals("BOLOGNA C.LE")) mi.put(trenodata, rit);

			}br.close();

		}
		return mr;
	}
	
public static void print(String file,  Set<String>m) throws Exception{  
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));  
		out.println("Nr_Treno\tStazione\tArrivo_Programmato\tArrivo_Effettivo\tRitardo_In_Minuti");
	
			
			for(String si : m){
				out.println(si);
			}
			
		 
		out.close(); 
	}

public static void print(String file,  Map<String, Map<String, String>>m) throws Exception{  
	
	PrintWriter out = new PrintWriter(new FileWriter(new File(file)));  
	out.println("Giorno\tNr_treno\tDataOra\tRitardo_In_Minuti");

		
		for(String si : m.keySet()){
			
			for (String sj : m.get(si).keySet()){
				out.print(si+"\t");
				out.println(sj+"\t"+m.get(si).get(sj));
			}
		
		}
		
	 
	out.close(); 
}

public static void printAlt(String file,  Map<String, Map<String, String>>m) throws Exception{  

	PrintWriter out = new PrintWriter(new FileWriter(new File(file)));  

	Map<String, Integer>mi = new TreeMap<String, Integer>();
	int counter = 0; 
	for(String si : m.keySet()){
		for (String sj : m.get(si).keySet()){
			String [] r = sj.split("\t");
			mi.put(r[0], counter++);
		}
	}
	out.print("Giorno,");
	int co = 0;
	
	for(String s : mi.keySet()){
		co++;
		String [] r = s.split("\t");
		if(co== mi.size()) out.print(r[0]);
		else out.print(r[0]+",");
	}
	out.println();
	
	for(String si : m.keySet()){
		System.out.println("--> "+si);
		int coi = 0;
		out.print(si+",");
		for (String sj : m.get(si).keySet()){
			coi++;
			if(coi == m.get(si).size()){
				String [] r = m.get(si).get(sj).split("\t");
				out.print(r[0]);
			}
			else out.print(m.get(si).get(sj)+",");
        }
       out.println();
	}
	out.close(); 
}
}