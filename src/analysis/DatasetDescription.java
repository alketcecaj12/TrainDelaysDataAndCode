package analysis;

import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import model.Record;

public class DatasetDescription {
	
	public static String [] trains = {"B2F",  "F2B",  "B2R",  "R2B",  "M2B",  "B2M", 
			"RA2B", "B2RA", "RI2B", "B2RI", "B2FI", "FI2B"};
	
	public static String [] months = {"Febbruary",  "January",  "October",  "November", "December"};
	
	public static void main (String [] args) throws Exception{
		
		double min_delay = 0.0; 
		double max_delay = 900.0; 
		
		
		
		Set<String> nr_stations = new HashSet<String>();
		Set<String> nr_days = new HashSet<String>();
		
		DescriptiveStatistics ds = new DescriptiveStatistics();
		
		Set<String> nr_trains = new HashSet<String>();
		List<String>travels = new ArrayList<String>();
        Map<String, DescriptiveStatistics>stzmap = new HashMap<String, DescriptiveStatistics>();
        
		for(int k = 0; k < months.length; k++){
			String files = "/Users/alket/Desktop/dataByMonth/B2F/"+months[k];
			//String files = "dataViaggiaTreno/"+trains[k];
            
			System.out.println(files);
			Map<String, Map<String, LinkedList<Record>>>m = RegressionAnalysis.loadData(files, max_delay, min_delay);

			System.out.println("-------------------------------------------------------- "+months[k]);
             int count = 0;
			for(String s : m.keySet()){
				
				for(String s2: m.get(s).keySet()){
					
					for(int i = 0; i < m.get(s).get(s2).size(); i++){
						
						String station_i = m.get(s).get(s2).get(i).getStz();
						nr_stations.add(station_i);
						
						String train_i = m.get(s).get(s2).get(i).getNrtreno();
						nr_trains.add(train_i);
						
						nr_days.add(s);
						
						double ritardo = m.get(s).get(s2).get(i).getRitardo(); 
						if (ritardo < 0) continue;
						ds.addValue(ritardo);
						DescriptiveStatistics dsi = stzmap.get(station_i);
						if(dsi == null) {
							dsi = new DescriptiveStatistics();
							dsi.addValue(ritardo);
							stzmap.put(station_i, dsi);
						}
						else dsi.addValue(ritardo);
						
					}
					count++;
					String travel = s+" "+s2+" "+count;
					travels.add(travel);
				}
			}
		
		Map<String, Double>mii = new HashMap<String,Double>();
		
		for(String s: stzmap.keySet()) {
			mii.put(s, round(stzmap.get(s).getMean(),2));
		   // System.out.println(s+" "+stzmap.get(s).getMean()+" "+stzmap.get(s).getN());	
		}
		
		
		HashMap<String, Double>mi = sortByValues(mii);
		
		for(String s: mi.keySet()) {
			if(stzmap.get(s).getN() > 2)
			 System.out.println(s+","+mi.get(s)+","+round(stzmap.get(s).getStandardDeviation(),2));	
		}
		
		
		System.out.println(ds.getN());
		ds = removeOutliers(ds);
		System.out.println(ds.getN());
		System.out.println("mean delay = "+ds.getMean()+", std.dev = "+ds.getStandardDeviation());
		System.out.println("nr travels = "+travels.size());
		System.out.println("nr of stations = "+nr_stations.size());
		System.out.println("nr of days = "+nr_days.size());
		
		System.out.println("nr of trains = "+nr_trains.size());
		}
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	public static DescriptiveStatistics removeOutliers(DescriptiveStatistics ds) {
		
		DescriptiveStatistics ret = new DescriptiveStatistics();
		DescriptiveStatistics d = new DescriptiveStatistics();
		
		double Q1 = ds.getPercentile(25.0);
		double Q3 = ds.getPercentile(75.0);
		double IQR = Q3-Q1;
		double min = Q1 - (IQR * 1.5);
		double max = Q3 + (IQR * 1.5);
		
		for(int i = 0; i < ds.getN(); i++) {
			if(ds.getElement(i) > min && ds.getElement(i)< max) {
				ret.addValue(ds.getElement(i));
			}
			else d.addValue(ds.getElement(i));
		}
		System.out.println(d);
		
		return ret;
	}
	
	private static HashMap sortByValues(Map map) { 
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

}
