package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import model.Record;

public class RegressionAnalysis {


	public static String [] trains = {"B2F",  "F2B",  "B2R",  "R2B",  "M2B",  "B2M", 
			"RA2B", "B2RA", "RI2B", "B2RI", "B2FI", "FI2B"};

	public static void main (String [] args)throws Exception{
		double min_delay = 0.0; 
		double max_delay = 30.0; 

		String delay_type = "delays0-30";

		for(int k = 0; k < trains.length; k++){

			String files = "dataViaggiaTreno/"+trains[k];

			Map<String, Map<String, LinkedList<Record>>>m = loadData(files, max_delay, min_delay);

			System.out.println("-------------------------------------------------------- "+trains[k]+", "+m.size());
			
			Map<String , List<String>> map_ret = new HashMap<String, List<String>>();

			Map<String, SimpleRegression> regression_map = new HashMap<String, SimpleRegression>();
			Map<String, Double> corr_map = new TreeMap<String, Double>();
			
			map_ret = Main2Analysis.getStations(m, 0, 1);
			//map_ret = getDelaysForThreeStations(m, 0, 1, 2);
			//map_ret = getDelays4FourStations(m, 0,1,2,3);
			int sample_size = 40;
			for(String s : map_ret.keySet()) {
				System.out.println("Stz. "+s+" >>>>>>>>>>>>>>>> "+map_ret.get(s).toString());
				String s2 = s.replace("/", "");
				
				if(map_ret.get(s).size() < sample_size) continue;
			    printFilesForReg(s2, "MregressionAnalysis/due/entries"+sample_size+"/AllRegData_"+delay_type+"_Stz14-15.csv", map_ret.get(s), sample_size);
				//printFilesForMultipleStz(s2, "MregressionAnalysis/quattro/entries"+sample_size+"/AllRegData"+s2+"_"+delay_type+"_Stz0-1-2.csv", map_ret.get(s), sample_size);
				//System.out.println(s+"++-->"+map_ret.get(s));
				Covariance co = new Covariance();
				SimpleRegression sr = new SimpleRegression();
				double [] v1 = new double[100];
				double [] v2 = new double[100];
				int l = 0;
				int j = 0;
				for(int i = 0; i < map_ret.get(s).size(); i++) {
					String [] r = map_ret.get(s).get(i).split(",");
				    double e1 = Double.parseDouble(r[0]);
				    double e2 = Double.parseDouble(r[1]);
                     
				    sr.addData(e1, e2);
				  
				    v1[l]= e1;
				    v2[j] = e2;
				    l++;
				    j++;
				    if(j == 100) break;
				    
				}
				regression_map.put(s2, sr);
				corr_map.put(s2, new PearsonsCorrelation().correlation(v1, v2));
			}
			for(String s : regression_map.keySet()) {
				
				System.out.println("intercept of reg.line "+s+" "+regression_map.get(s).getIntercept());
				// displays intercept of regression line

				System.out.println("slope of reg.line "+s+" "+regression_map.get(s).getSlope());
				// displays slope of regression line

				System.out.println("slope std. err. "+s+" "+regression_map.get(s).getSlopeStdErr());
				System.out.println("For a 5.0 minutes delay at "+s.substring(0, s.indexOf(","))+" "
						+ "the model predicts = "+regression_map.get(s).predict(5.0)+" delay at "+s.substring(s.indexOf(","), s.length()));
				System.out.println("--------------------------------------------------");
			}
			for(String s : corr_map.keySet()) {
				System.out.println("La coppia di stazioni "+s+" hanno cf.corr  =  "+corr_map.get(s)+"per i loro ritardi");
			}
			break;
		}
	}
	  public static void printFilesForReg(String header, String file, List<String>list, int sample_size)throws Exception{
		  int count = 0;
		  PrintWriter out = new PrintWriter(new FileWriter(new File(file), true));
		  
		  for(int i = 0; i < list.size(); i++) {
			  count++;
			  out.println(list.get(i));
			  if(count==sample_size) break;
		  }
		  out.close();
	  }
	  public static void printFilesForMultipleStz(String header, String file, List<String>list, int sample_size)throws Exception{
		  int count = 0;
		  PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		  out.println(header);
		  for(int i = 0; i < list.size(); i++) {
			  count++;
			  out.println(list.get(i));
			  if(count==sample_size) break;
		  }
		  out.close();
	  }
	public static Map<String,List<String>> getDelaysForThreeStations(Map<String, Map<String, LinkedList<Record>>>m, int stz_i, int stz_i_più1, int stz_i_più2){
		  
		  Map<String,List<String>> ret_m = new HashMap<String, List<String>>();
		  
		  for(String s : m.keySet()) {
			  
			  String stz = "";
			  String stz_successiva = "";
			  String stz_successivapiù1 = "";
			  double delay_i = 0;
			  double delay_ipiù1 = 0;
			  double delay_ipiù2 = 0;
			  String nrtreno = "";
			  for(String s2 : m.get(s).keySet()) { 
				 
			        if(m.get(s).get(s2).size()<= stz_i_più2) continue;
		        	     stz = m.get(s).get(s2).get(stz_i).getStz();
		        	     stz_successiva = m.get(s).get(s2).get(stz_i_più1).getStz();
		        	     stz_successivapiù1 = m.get(s).get(s2).get(stz_i_più2).getStz();
		        	     delay_i = m.get(s).get(s2).get(stz_i).getRitardo();
		        	     delay_ipiù1 =  m.get(s).get(s2).get(stz_i_più1).getRitardo();
		        	     delay_ipiù2 = m.get(s).get(s2).get(stz_i_più2).getRitardo();
		        	     nrtreno =  m.get(s).get(s2).get(stz_i_più1).getNrtreno();
		        	     //System.out.println(s+" --> "+nrtreno+" "+stz+","+stz_successiva+","+delay_i+","+delay_ipiù1);
		             List<String> li = ret_m.get(stz+","+stz_successiva+","+stz_successivapiù1);
		             
		             if(li == null) {
		            	    li = new ArrayList<String>();
		            	    li.add(delay_i+","+delay_ipiù1+","+delay_ipiù2);
		            	    ret_m.put(stz+","+stz_successiva+","+ stz_successivapiù1, li);
		             }
		             else li.add(delay_i+","+delay_ipiù1+","+delay_ipiù2);
			  }
			
		  }
		  return ret_m; 
		  
	}
	
	public static Map<String,List<String>> getDelays4FourStations(Map<String, Map<String, LinkedList<Record>>>m, int stz_i, int stz_i_più1, int stz_i_più2, int stz_i_più3){
		  
		  Map<String,List<String>> ret_m = new HashMap<String, List<String>>();
		  
		  for(String s : m.keySet()) {
			  
			  String stz = "";
			  String stz_successiva = "";
			  String stz_successivapiù1 = "";
			  String stz_successivapiù2 = "";
			  double delay_i = 0;
			  double delay_ipiù1 = 0;
			  double delay_ipiù2 = 0;
			  double delay_ipiù3 = 0;
			  String nrtreno = "";
			  for(String s2 : m.get(s).keySet()) { 
				 
			        if(m.get(s).get(s2).size()<= stz_i_più2) continue;
		        	     stz = m.get(s).get(s2).get(stz_i).getStz();
		        	     stz_successiva = m.get(s).get(s2).get(stz_i_più1).getStz();
		        	     stz_successivapiù1 = m.get(s).get(s2).get(stz_i_più2).getStz();
		        	     delay_i = m.get(s).get(s2).get(stz_i).getRitardo();
		        	     delay_ipiù1 =  m.get(s).get(s2).get(stz_i_più1).getRitardo();
		        	     delay_ipiù2 = m.get(s).get(s2).get(stz_i_più2).getRitardo();
		        	     delay_ipiù3 = m.get(s).get(s2).get(stz_i_più2).getRitardo();
		        	     nrtreno =  m.get(s).get(s2).get(stz_i_più1).getNrtreno();
		        	     //System.out.println(s+" --> "+nrtreno+" "+stz+","+stz_successiva+","+delay_i+","+delay_ipiù1);
		             List<String> li = ret_m.get(stz+","+stz_successiva+","+stz_successivapiù1+","+stz_successivapiù2);
		             
		             if(li == null) {
		            	    li = new ArrayList<String>();
		            	    li.add(delay_i+","+delay_ipiù1+","+delay_ipiù2+","+delay_ipiù3);
		            	    ret_m.put(stz+","+stz_successiva+","+ stz_successivapiù1+","+stz_successivapiù2, li);
		             }
		             else li.add(delay_i+","+delay_ipiù1+","+delay_ipiù2+","+delay_ipiù3);
			  }
			
		  }
		  return ret_m; 
		  
	}
	
	
	public static Map<String, Map<String, LinkedList<Record>>>loadData(String files, double max_rit, double min_rit) throws Exception{
		Map<String, Map<String, LinkedList<Record>>> l = new HashMap<String, Map<String, LinkedList<Record>>>();
		
		File f = new File(files);
		
		File [] folder = f.listFiles(); 
		
		System.out.println("folder length = "+folder.length);
		for(int i = 0; i < folder.length; i++){

			String filename = folder[i].getName().substring(23, 33);

			Map<String, LinkedList<Record>>mi = l.get(filename);
			
			if(mi == null){
				mi = new HashMap<String, LinkedList<Record>>();
				l.put(filename, mi);
			}

			BufferedReader br = new BufferedReader(new FileReader(folder[i]));

			String line; 
			br.readLine();
			while((line = br.readLine())!= null){

				String [] r = line.split("\t");
               // System.out.println(r.length);
                
                String treno = r[0];
                String city = r[1];
                String arrivo_p = r[2];
                String arrivo_e = r[3];
        		double rit = Double.parseDouble(r[4]);
        		
        		if(rit > min_rit && rit < max_rit) {
				LinkedList<Record> lli = mi.get(treno);
				
				if(lli == null){
					lli = new LinkedList<Record>();
					mi.put(treno, lli);
				}
				
				lli.add(new Record(treno, city, arrivo_p, arrivo_e, rit));
	          		
        		}
			}br.close();
		}
		return l; 		
	}
	
}
