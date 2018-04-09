package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import model.Record;

public class Main2Record {

	public static String [] trains = {"B2F",  "F2B",  "B2R",  "R2B",  "M2B",  "B2M", 
			                          "RA2B", "B2RA", "RI2B", "B2RI", "B2FI", "FI2B"};
	
	public static void main (String [] args)throws Exception{
      double min_delay = 0.0; 
      double max_delay = 600.0; 
		
      String delay_type = "delays0-600";
		
		for(int k = 0; k < trains.length; k++){
			
			String files = "dataViaggiaTreno/"+trains[k];

			Map<String, Map<String, LinkedList<Record>>>m = loadData(files);
            
			System.out.println("-------------------------------------------------------- "+trains[k]);

			Main2Analysis.getStatsOnDelays(m, max_delay, min_delay);
			//printAllDelay("/Users/alket/rstudio/"+delay_type+"/allDelays/AllLinesDelays.csv", m, max_delay, min_delay);
			printAllDelayPerLine("/Users/alket/rstudio/"+delay_type+"/delaysPerLine/AllLinesDelays_"+trains[k]+"_.csv", m, max_delay, min_delay);
			// mean of delays agg. by day
			Object [] DailyStatsOnDelays = new Object [2];
			DailyStatsOnDelays = Main2Analysis.getDailyStatsOnDelays(m, max_delay, min_delay);
			Map<String, Double>stats = new HashMap<String, Double>();
			DescriptiveStatistics descriptive = new DescriptiveStatistics();
			stats = (Map)DailyStatsOnDelays[0];
			descriptive= (DescriptiveStatistics)DailyStatsOnDelays[1];
            printMeanDailyDelay("/Users/alket/rstudio/"+delay_type+"/dailyMeanDelay/MeanDailyDelays_"+trains[k]+".csv",descriptive);
            
            // mean of delay aggregated per day of week
			Object [] StatsByDayOfWeek = new Object [2];
			StatsByDayOfWeek = Main2Analysis.getStatsByDayOfWeek(m, max_delay, min_delay);
			Map<String, Double>statsbyDayOfWeek = new HashMap<String, Double>();
			Map<Integer, DescriptiveStatistics> prov_stats = new HashMap<Integer, DescriptiveStatistics>();
			prov_stats = (Map)StatsByDayOfWeek[0];
			statsbyDayOfWeek = (Map)StatsByDayOfWeek[1];
			printMeanDealyPerDayOfWeek("/Users/alket/rstudio/"+delay_type+"/delaysPerDayOfWeek/AllData_weeklyDelays"+trains[k]+".csv",statsbyDayOfWeek );
			
			// stats of data aggregated by number of train
			Object [] StatsByNrOfTrain = new Object[2];
			StatsByNrOfTrain = Main2Analysis.getStatsByNumberOfTrain(m, max_delay, min_delay);
			HashMap<String, Double>statsByNrOfTrain = new HashMap<String, Double>();
			Map<Integer, DescriptiveStatistics>statInterval = new HashMap<Integer, DescriptiveStatistics>();
			statsByNrOfTrain = (HashMap)StatsByNrOfTrain[1];
			statInterval = (Map)StatsByNrOfTrain[0];
			//sort values
			HashMap<String, Double>statsByNrOfTrain2 = sortByValues2(statsByNrOfTrain);
			printMeanDealyByTrainNr("/Users/alket/rstudio/"+delay_type+"/delayPerTrainNr/AllData_delayNrTrain"+trains[k]+".csv", statsByNrOfTrain2);
			
			// stats of data aggregated per time slot. 
			Object [] StatsByTimeSlot = new Object[2];
			StatsByTimeSlot = Main2Analysis.getStatsByTimeSlot(m, max_delay, min_delay);
			Map<String, Double>statsByTimeSlot = new TreeMap<String, Double>();
			Map<String, DescriptiveStatistics>statTimeSlotInterval = new HashMap<String, DescriptiveStatistics>();
			statTimeSlotInterval= (Map)StatsByTimeSlot[0];
			statsByTimeSlot = (Map)StatsByTimeSlot[1];
		    printMeanDelayPerTimeSlot("/Users/alket/rstudio/"+delay_type+"/delayPerTimeSlot/delayPerTimeSlot_"+trains[k]+".csv", statsByTimeSlot);

			printTablesPerLine("/Users/alket/rstudio/"+delay_type+"/delayDataTable/AllDataTables_"+trains[k]+"_table.csv", m);
			
			Map<String, DescriptiveStatistics> delayPerStation = new HashMap<String, DescriptiveStatistics>();
			delayPerStation = Main2Analysis.getMeanDelayAtStz(m, max_delay, min_delay);
			HashMap<String, Double>prov_map = new HashMap<String, Double>();
			
			for(String s : delayPerStation.keySet())
			   prov_map.put(s, delayPerStation.get(s).getPercentile(50));
			
			Map<String, Double>sortedmap = new HashMap<String, Double>();
			sortedmap = sortByValues2(prov_map);
			printMeanDelayPerStation("/Users/alket/rstudio/"+delay_type+"/delayPerStation/DelaysPerStationAt_"+trains[k]+"_.csv", sortedmap);
			
			Map<String, DescriptiveStatistics>agg_h = new TreeMap<String, DescriptiveStatistics>();
			agg_h =  Main2Analysis.getMeanDelayAggByHour(m , max_delay, min_delay);
			printDelayPerHour("/Users/alket/rstudio/"+delay_type+"/delayPerHour/MeanDelayPerHourAt_"+trains[k]+"_.csv", agg_h);
			
			Map<String , List<String>> map_ret = new HashMap<String, List<String>>();
			map_ret = Main2Analysis.getStations(m, 0, 1);
			int sample_size = 140;
			for(String s : map_ret.keySet()) {
				String s2 = s.replace("/", "");
				if(map_ret.get(s).size() < sample_size) continue;
				printFilesForReg(s2, "regressionAnalysis/entries"+sample_size+"/RegData_"+s2+"_.csv", map_ret.get(s));
				System.out.println(s+"++-->"+map_ret.get(s));
			}
		}
	}
	 private static HashMap sortByValues2(HashMap map) { 
	       List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o2, Object o1) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       });

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	  }

	
// print files for regression analysis
	  public static void printFilesForReg(String header, String file, List<String>list)throws Exception{
		  int count = 0;
		  PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		  out.println(header);
		  for(int i = 0; i < list.size(); i++) {
			  count++;
			  out.println(list.get(i));
			  if(count==100) break;
		  }
		  out.close();
	  }
	  
	  
public static void printAllDelay(String file, Map<String, Map<String, LinkedList<Record>>>m, double max_delay, double min_delay)throws Exception{
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file), true));
		
	    for(String s : m.keySet()){
			
			for(String s2: m.get(s).keySet()){
				
				for(int i = 0; i < m.get(s).get(s2).size(); i++){
					
					double ritardo = m.get(s).get(s2).get(i).getRitardo();
					
					if(ritardo > min_delay && ritardo < max_delay) out.println(m.get(s).get(s2).get(i).getRitardo());
					
				}
			}
		}

		out.close();
		
	}

public static void printAllDelayPerLine(String file, Map<String, Map<String, LinkedList<Record>>>m, double max_delay, double min_delay)throws Exception{
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		out.println("delay");
		
	    for(String s : m.keySet()){
			
			for(String s2: m.get(s).keySet()){
				
				for(int i = 0; i < m.get(s).get(s2).size(); i++){
					
					double ritardo = m.get(s).get(s2).get(i).getRitardo();
					
					if(ritardo > min_delay && ritardo < max_delay) out.println(m.get(s).get(s2).get(i).getRitardo());
					
				}
			}
		}

		out.close();
		
	}

public static void printDelayPerHour(String file, Map<String, DescriptiveStatistics>agg_h )throws Exception{
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		Map<String, Double>h = new TreeMap<String, Double> ();
		for(String s: agg_h.keySet())
			h.put(s, agg_h.get(s).getPercentile(50));
		
		out.println("hour,mean_delay");
		for(String s : h.keySet() ){
			out.println(s+","+h.get(s));
		}
		
		out.close();	
}

public static void printDelayPerStation(String file, Map<String, DescriptiveStatistics>statsbyTimeSlot )throws Exception{
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		out.println("station,mean_delay,std_dev");
		
		for(String s : statsbyTimeSlot.keySet() ){
		  
			out.println(s+","+statsbyTimeSlot.get(s).getMean()+","+statsbyTimeSlot.get(s).getStandardDeviation());
			
		}
		
		out.close();
		
	}
public static void printMeanDelayPerStation(String file, Map<String, Double>statsbyTimeSlot )throws Exception{
	
	PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
	out.println("station,mean_delay");
	int count = 0;
	for(String s : statsbyTimeSlot.keySet() ){
	   
		if (count < 20 )out.println(s+","+statsbyTimeSlot.get(s));
		count++;
	}
	
	out.close();
	
}
	public static void printMeanDelayPerTimeSlot(String file, Map<String, Double>statsbyTimeSlot )throws Exception{
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		out.println("day,timeslotdelay");
		
		for(String s : statsbyTimeSlot.keySet() ){
		  
			out.println(s+","+statsbyTimeSlot.get(s));
			
		}
		
		out.close();
		
	}
	
	
	public static void printTimeSlotDelayInterval(	String file,  DescriptiveStatistics statTimeSlotInterval) throws Exception{
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		out.println("delay");
		for(int i = 0; i < statTimeSlotInterval.getN(); i++){
		     out.println(statTimeSlotInterval.getElement(i));
		}
		
		out.close();
		
		
	}
	
	public static void printTablesPerLine(String file, Map<String, Map<String, LinkedList<Record>>>m )throws Exception{
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		
        out.println("data\ttreno\tstazione\tarr_prog\tarr_eff\tritardo");
		
	    for(String s : m.keySet()){
			
			for(String s2: m.get(s).keySet()){
				
				for(int i = 0; i < m.get(s).get(s2).size(); i++){
					
					out.println(s+","+s2+"\t"+m.get(s).get(s2).get(i).getStz()+"\t"+m.get(s).get(s2).get(i).getArrivo_programmato()+"\t"
							+ ""+m.get(s).get(s2).get(i).getArrivo_effettivo()+"\t"+m.get(s).get(s2).get(i).getRitardo());
					
					
				}
			}
		}
		
		
		out.close();
		
	}
	
	
	public static void printAllDelay(String file, Map<String, Map<String, LinkedList<Record>>>m )throws Exception{
		PrintWriter out = new PrintWriter(new FileWriter(new File(file), true));
		

	    for(String s : m.keySet()){
			
			for(String s2: m.get(s).keySet()){
				
				for(int i = 0; i < m.get(s).get(s2).size(); i++){
					
					out.println(m.get(s).get(s2).get(i).getRitardo());
					
					
				}
			}
		}
		
		
		out.close();
		
	}
	
	public static void printMeanDailyDelay(String file, DescriptiveStatistics descriptive )throws Exception{
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		out.println("delay");
		for(int i = 0; i < descriptive.getN(); i++){
		     out.println(descriptive.getElement(i));
		}
		
		out.close();
		
	}
	
	public static void printMeanDealyPerDayOfWeek(String file, Map<String, Double>statsbyDayOfWeek )throws Exception{
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		out.println("day, week_delay");
		for(String s : statsbyDayOfWeek.keySet() ){
			int key = Integer.parseInt(s);
			
			if(key == 1)
		     out.println("Sun"+","+statsbyDayOfWeek.get(s));
			if(key == 2)
			     out.println("Mon"+","+statsbyDayOfWeek.get(s));
			if(key == 3)
			     out.println("Tue"+","+statsbyDayOfWeek.get(s));
			if(key == 4)
				     out.println("Wed"+","+statsbyDayOfWeek.get(s));
			if(key == 5)
			     out.println("Thu"+","+statsbyDayOfWeek.get(s));
			if(key == 6)
				     out.println("Fri"+","+statsbyDayOfWeek.get(s));	
			if(key == 7)
			     out.println("Sat"+","+statsbyDayOfWeek.get(s));	
		}
		
		out.close();
		
	}

	public static void printMeanDealyByTrainNr(String file, Map<String, Double>statsbyNrOfTrain )throws Exception{
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		out.println("trainNr, delay");
		for(String s : statsbyNrOfTrain.keySet() ){
			out.println(s+","+statsbyNrOfTrain.get(s));
		}
		
		out.close();
		
	}
	public static void printDataTimeSlots(String file, Map<String, Double>statsbyTimeSlot )throws Exception{
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));
		out.println("timeslot,delay");
		
		for(String s : statsbyTimeSlot.keySet() ){
		    String  s2 = s.substring(3,s.length() );
		  
			out.println(s2+","+statsbyTimeSlot.get(s));
			
		}
		
		out.close();
		
	}
	
	public static Map<String, Map<String, LinkedList<Record>>>loadData(String files) throws Exception{
		Map<String, Map<String, LinkedList<Record>>> l = new HashMap<String, Map<String, LinkedList<Record>>>();
		
		
		File f = new File(files);
		
		File [] folder = f.listFiles(); 
		
		//System.out.println("folder length = "+folder.length);
		
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
        		
				LinkedList<Record> lli = mi.get(treno);
				
				if(lli == null){
					lli = new LinkedList<Record>();
					mi.put(treno, lli);
				}
				
				lli.add(new Record(treno, city, arrivo_p, arrivo_e, rit));
		
			}br.close();

		}
		
		
		return l; 
		
	}
}
