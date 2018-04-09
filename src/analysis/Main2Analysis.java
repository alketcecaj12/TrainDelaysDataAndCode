package analysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import model.Record;

public class Main2Analysis {
	
	public static void getStatsOnDelays(Map<String, Map<String, LinkedList<Record>>>m, double rMax, double rMin){
	
	DescriptiveStatistics d = new DescriptiveStatistics();
	
	    for(String s : m.keySet()){
			
			for(String s2: m.get(s).keySet()){
				
				for(int i = 0; i < m.get(s).get(s2).size(); i++){
					
					//System.out.println(s+"----> "+s+" "+s2+" "+m.get(s).get(s2).get(i).getStz()+" "+m.get(s).get(s2).get(i).getRitardo());
					double ritardo = m.get(s).get(s2).get(i).getRitardo(); 
					if (ritardo < rMax && ritardo > rMin)
					d.addValue(m.get(s).get(s2).get(i).getRitardo());
				}
			}
		}
		
System.out.println("max = "+ d.getMax()+"\n"
		          + "min = " +d.getMin()+"\n"
		          + "mean = "+d.getMean()+"\n"
		          + "dev.std = "+d.getStandardDeviation()+"\n"
		          + "num "+d.getN());
		
	}
	
	public static Object[] getDailyStatsOnDelays(Map<String, Map<String, LinkedList<Record>>>m, double rMax, double rMin){
		
	DescriptiveStatistics d = new DescriptiveStatistics();
	
	Map<String, Double> stats = new HashMap<String, Double>();
	Object [] o = new Object[2];
	
	    for(String s : m.keySet()){
			
			for(String s2: m.get(s).keySet()){
				
				for(int i = 0; i < m.get(s).get(s2).size(); i++){
					
					//System.out.println("--> "+s+" "+s2+" "+m.get(s).get(s2).get(i).getStz()+" "+m.get(s).get(s2).get(i).getRitardo());
					double ritardo = m.get(s).get(s2).get(i).getRitardo(); 
					if (ritardo < rMax && ritardo > rMin)
					d.addValue(m.get(s).get(s2).get(i).getRitardo());
				}
			}
			double mean = d.getPercentile(50.0);
			if(Double.isNaN(mean)) mean = 0;
			stats.put(s, mean);
		}
		o[0] = stats;
		o[1] = d;
        return o;
		
	}
	
	public static Object [] getStatsByDayOfWeek(Map<String, Map<String, LinkedList<Record>>>m, double rMax, double rMin) throws Exception{
		Object [] o = new Object[2];
		DescriptiveStatistics d = new DescriptiveStatistics();
		Map<String, Double> stats = new HashMap<String, Double>();
		
		Map<Integer, DescriptiveStatistics> prov_stats = new HashMap<Integer, DescriptiveStatistics>();
		
		    for(String s : m.keySet()){
		    	
		    	
		    	Calendar c = Calendar.getInstance();
				Date date = new Date();
				String input_date=s;
				SimpleDateFormat format1=new SimpleDateFormat("dd-MM-yyyy");
				date =format1.parse(input_date);
				c.setTime(date);
				int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
				//System.out.println("giorno della settimana "+dayOfWeek+" in data "+s);
				
				DescriptiveStatistics ds = prov_stats.get(dayOfWeek);
				if(ds == null){
					ds = new DescriptiveStatistics();
				    prov_stats.put(dayOfWeek, ds);	
				}
				
				
				for(String s2: m.get(s).keySet()){
					
					for(int i = 0; i < m.get(s).get(s2).size(); i++){
						
						//System.out.println("--> "+s+" "+s2+" "+m.get(s).get(s2).get(i).getStz()+" "+m.get(s).get(s2).get(i).getRitardo());
						double ritardo = m.get(s).get(s2).get(i).getRitardo(); 
						if (ritardo < rMax && ritardo > rMin)
						ds.addValue(m.get(s).get(s2).get(i).getRitardo());
					}
				}
				
				for(Integer i : prov_stats.keySet()){
					double mean =prov_stats.get(i).getPercentile(50.0);
					if(Double.isNaN(mean)) mean = 0;
					stats.put(i.toString(), mean);
				}
				
			}
			o[0] = prov_stats;
			o[1] = stats; 
	        return o;
			
		}
	
	
	
public static Object[] getStatsByNumberOfTrain(Map<String, Map<String, LinkedList<Record>>>m, double rMax, double rMin) throws Exception{
		
	    Object[] o = new Object[2];
	    
		DescriptiveStatistics ds = new DescriptiveStatistics();
		Map<String, Double> stats = new HashMap<String, Double>();
		
		Map<Integer, DescriptiveStatistics> prov_stats = new HashMap<Integer, DescriptiveStatistics>();
		
		    for(String s : m.keySet()){

				for(String s2: m.get(s).keySet()){
					
					for(int i = 0; i < m.get(s).get(s2).size(); i++){
						
						int train_number = Integer.parseInt(m.get(s).get(s2).get(i).getNrtreno());
						DescriptiveStatistics di = prov_stats.get(train_number);
						if(di == null){
							di = new DescriptiveStatistics();
							prov_stats.put(train_number, di);
						}
						//System.out.println("--> "+s+" "+s2+" "+m.get(s).get(s2).get(i).getStz()+" "+m.get(s).get(s2).get(i).getRitardo());
						double ritardo = m.get(s).get(s2).get(i).getRitardo(); 
						if (ritardo < rMax && ritardo > rMin)
						di.addValue(m.get(s).get(s2).get(i).getRitardo());
					}
				}
				
				for(Integer i : prov_stats.keySet()){
					double mean =prov_stats.get(i).getPercentile(50.0);
					if(Double.isNaN(mean)) mean = 0;
					stats.put(i.toString(), mean);
				}
				
			}
			o[0] = prov_stats;
			o[1] = stats;
			
	        return o;
			
		}
	
public static Object[] getStatsByTimeSlot(Map<String, Map<String, LinkedList<Record>>>m, double rMax, double rMin) throws Exception{
	
	
	Object [] o = new Object[2];
	DescriptiveStatistics ds = new DescriptiveStatistics();
	
	Map<String, Double> stats = new HashMap<String, Double>();
	Map<String, Double> mapd = new HashMap<String, Double>();
	
	Map<String, DescriptiveStatistics> prov_stats = new HashMap<String, DescriptiveStatistics>();
	
	    for(String s : m.keySet()){
        
			for(String s2: m.get(s).keySet()){
				
				for(int i = 0; i < m.get(s).get(s2).size(); i++){
					
					String date = m.get(s).get(s2).get(i).getArrivo_programmato();
					//30/10/17 07.27.00
					String [] r = date.split(" ");
					//System.out.println(r[1]);
					int ora = Integer.parseInt(r[1].substring(0, 2));
					//System.out.println("ora --> "+ora);
					
					double ritardo = m.get(s).get(s2).get(i).getRitardo(); 
					
					if (ritardo < rMax && ritardo > rMin){
						if(ora >= 0 && ora <=5){
							DescriptiveStatistics di = prov_stats.get("0-5");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("0-5", di);
							}
							di.addValue(ritardo);
						}
						if(ora > 5 && ora <= 9){
							DescriptiveStatistics di = prov_stats.get("6-9");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("6-9", di);
							}
							di.addValue(ritardo);
						}
						if(ora > 9 && ora <= 14){
							DescriptiveStatistics di = prov_stats.get("10-14");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("10-14", di);
							}
							di.addValue(ritardo);
						}
						if(ora >= 15 && ora <= 19){
							DescriptiveStatistics di = prov_stats.get("15-19");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("15-19", di);
							}
							di.addValue(ritardo);
						}
						if(ora >= 20 && ora <=23){
							DescriptiveStatistics di = prov_stats.get("20-23");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("20-23", di);
							}
							di.addValue(ritardo);
						}
						
					}
				}
			}
			
			for(String sii : prov_stats.keySet()){
				double mean =prov_stats.get(sii).getPercentile(50.0);
				if(Double.isNaN(mean)) mean = 0;
				stats.put(sii, mean);
			}
			
		}
		o[0] = prov_stats; 
		o[1] = stats;
		return o;
	}

public static Map<String, DescriptiveStatistics> getMeanDelayAggByHour(Map<String, Map<String, LinkedList<Record>>>m, double rMax, double rMin) throws Exception{
	
	Map<String, DescriptiveStatistics> prov_stats = new HashMap<String, DescriptiveStatistics>();
	
	DescriptiveStatistics ds = new DescriptiveStatistics();
	
	for(String s : m.keySet()){

			for(String s2: m.get(s).keySet()){
				
				for(int i = 0; i < m.get(s).get(s2).size(); i++){
					
					String date = m.get(s).get(s2).get(i).getArrivo_programmato();
					String [] r = date.split(" ");
					int ora = Integer.parseInt(r[1].substring(0, 2));
					double ritardo = m.get(s).get(s2).get(i).getRitardo(); 
					
				  
					if (ritardo < rMax && ritardo > rMin){
						
						if(ora == 0){
							  DescriptiveStatistics di = prov_stats.get(ora);
							  if (di == null){
									di = new DescriptiveStatistics();
									prov_stats.put("0", di);
								}
								di.addValue(ritardo);
						}	
						if(ora == 1){
							DescriptiveStatistics di = prov_stats.get("1");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("1", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 2){
							DescriptiveStatistics di = prov_stats.get("2");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("2", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 3){
							DescriptiveStatistics di = prov_stats.get("3");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("3", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 4){
							DescriptiveStatistics di = prov_stats.get("4");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("4", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 5){
							DescriptiveStatistics di = prov_stats.get("5");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("5", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 6){
							DescriptiveStatistics di = prov_stats.get("6");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("6", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 7){
							DescriptiveStatistics di = prov_stats.get("7");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("7", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 8){
							DescriptiveStatistics di = prov_stats.get("8");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("8", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 9){
							DescriptiveStatistics di = prov_stats.get("9");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("9", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 10){
							DescriptiveStatistics di = prov_stats.get("10");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("10", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 11){
							DescriptiveStatistics di = prov_stats.get("11");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("11", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 12){
							DescriptiveStatistics di = prov_stats.get("12");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("12", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 13){
							DescriptiveStatistics di = prov_stats.get("13");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("13", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 14){
							DescriptiveStatistics di = prov_stats.get("14");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("14", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 15){
							DescriptiveStatistics di = prov_stats.get("15");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("15", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 16){
							DescriptiveStatistics di = prov_stats.get("16");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("16", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 17){
							DescriptiveStatistics di = prov_stats.get("17");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("17", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 18){
							DescriptiveStatistics di = prov_stats.get("18");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("18", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 19){
							DescriptiveStatistics di = prov_stats.get("19");
							
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("19", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 20){
							DescriptiveStatistics di = prov_stats.get("20");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("20", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 21){
							DescriptiveStatistics di = prov_stats.get("21");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("21", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 22){
							DescriptiveStatistics di = prov_stats.get("22");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("22", di);
							}
							di.addValue(ritardo);
						}
						if(ora == 23){
							DescriptiveStatistics di = prov_stats.get("23");
							if (di == null){
								di = new DescriptiveStatistics();
								prov_stats.put("23", di);
							}
							di.addValue(ritardo);
						}
					 }
					}
				}
	}
		
		return prov_stats;
	}
public static Map<String, DescriptiveStatistics> getMeanDelayAtStz(Map<String, Map<String, LinkedList<Record>>>m, double rMax, double rMin) throws Exception{
	
	Map<String, DescriptiveStatistics> prov_stats = new HashMap<String, DescriptiveStatistics>();
	
	    for(String s : m.keySet()){
           
	    	
			for(String s2: m.get(s).keySet()){
				
				for(int i = 0; i < m.get(s).get(s2).size(); i++){
					
					String stz = m.get(s).get(s2).get(i).getStz();
					DescriptiveStatistics dsi = prov_stats.get(stz);
					if(dsi == null){
						dsi = new DescriptiveStatistics();
						dsi.addValue(m.get(s).get(s2).get(i).getRitardo());
						prov_stats.put(stz, dsi);
					}
					else
					dsi.addValue(m.get(s).get(s2).get(i).getRitardo());
					
				}
			}
	    }
	    
	    return prov_stats;
}
public static Map<String,List<String>> getStations(Map<String, Map<String, LinkedList<Record>>>m, int stz_i, int stz_i_più1){
	  
	  Map<String,List<String>> ret_m = new HashMap<String, List<String>>();
	  
	  for(String s : m.keySet()) {
		  
		  String stz = "";
		  String stz_successiva = "";
		  double delay_i = 0;
		  double delay_ipiù1 = 0;
		  String nrtreno = "";
		  for(String s2 : m.get(s).keySet()) { 
			 
		        if(m.get(s).get(s2).size()<= stz_i_più1) continue;
	        	     stz = m.get(s).get(s2).get(stz_i).getStz();
	        	     stz_successiva = m.get(s).get(s2).get(stz_i_più1).getStz();
	        	     delay_i = m.get(s).get(s2).get(stz_i).getRitardo();
	        	     //if(delay_i > )
	        	     delay_ipiù1 =  m.get(s).get(s2).get(stz_i_più1).getRitardo();
	        	     nrtreno =  m.get(s).get(s2).get(stz_i_più1).getNrtreno();
	        	     //System.out.println(s+" --> "+nrtreno+" "+stz+","+stz_successiva+","+delay_i+","+delay_ipiù1);
	             List<String> li = ret_m.get(stz+","+stz_successiva);
	             
	             if(li == null) {
	            	    li = new ArrayList<String>();
	            	    li.add(delay_i+","+delay_ipiù1);
	            	    ret_m.put(stz+","+stz_successiva, li);
	             }
	             else li.add(delay_i+","+delay_ipiù1);
		  }
		
	  }
	  return ret_m; 
	  
}

}
