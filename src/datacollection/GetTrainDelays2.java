package datacollection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetTrainDelays2 {
	
	/*
	 * 
	 * parsin dei treni da Bologna - Ferrara e Ferrara - Bologna 
	 */
	
	public static String standardURL = "http://www.viaggiatreno.it/viaggiatrenonew/resteasy/viaggiatreno/"
			                         + "tratteCanvas";
	public static String standardURL2 = "http://www.viaggiatreno.it/viaggiatrenonew/resteasy/viaggiatreno/"
			                          + "andamentoTreno";	
	public static String getStzId ="http://www.viaggiatreno.it/viaggiatrenonew/resteasy/viaggiatreno/"
			                      + "cercaNumeroTrenoTrenoAutocomplete";
	
	public static void main (String [] args)throws Exception{
		
		
		 for(int j = 0; j < 365; j++){
			 System.out.println("day "+j);
			 for(int i = 0; i < 6; i++){
				 Date d = new Date();
				 System.out.println("iter "+i+" at "+d.toString());
				 run();
				 Thread.sleep(1000*60*60*2);
				 run();
			 }
		 }
	}
		
		public static void run () throws Exception{
		Map<String, List<Train>>m_B2R = new HashMap<String, List<Train>>();
		Map<String, List<Train>>m_R2B = new HashMap<String, List<Train>>();
		
		String [] trainsBo2Fe = {"35775","2222", "2224", "2226","2228", "2230", "2231","8408","11554",
				                 "11428", "11474", "6412", "6414", "11496", "2232", "11480", "9416", "2224", 
				                 "6422", "2236", "8816", "11422", "588", "2238", "11176", 
				                 "2240", "6430", "2242", "11484", "2244", "11486", "2246", 
				                 "6576", "8828", "592", "6590", "2248", "8464", "6426", "96220"}; 
		
		String [] trainsFe2Bo = {"35571", "11427", "2225", "11177", "11473", "6407", "11527", "2223", "2227",
                                 "2229", "6411", "11583", "6413", "8413", "2231", "6415", 
                                 "2233", "2235", "2237","2239", "11573", "11483", "11179", 
                                 "2141", "6423", "6417", "595", "8817", "2249"};
		
		List<Train>t_Bo2Fe = new ArrayList<>();
		System.out.println("BoFe****************************");
		
		for(int i = 0; i < trainsBo2Fe.length; i++ ){	
			
			System.out.println("Bologna --> Ferrara");	
			String getIdStz =  getJSON2(getStzId+"/"+trainsBo2Fe[i]);
		
			System.out.println("id stz "+getIdStz+ " for train "+trainsBo2Fe[i]);
			String [] r = getIdStz.trim().split("-");
			if(r.length < 2) continue;
			//System.out.println("Ri = r0 "+r[0]+",r1 "+r[1]+",r2 "+r[2]);
			String IdStz = r[2];
			if(IdStz.length()>6){
				IdStz = IdStz.substring(0,6);
				System.out.println("inside if == "+IdStz);
			}
			System.out.println(standardURL+"/"+IdStz.trim()+"/"+trainsBo2Fe[i].trim());
			try{
				String ViaggiaTreno_resp = getJSON2(standardURL+"/"+IdStz.trim()+"/"+trainsBo2Fe[i].trim());

				t_Bo2Fe = getTrains2(ViaggiaTreno_resp);
				m_B2R.put(trainsBo2Fe[i].toString(), t_Bo2Fe);
			
               }catch (Exception e){
			
		}
		}
		
		List<Train>t_Re2Bo = new ArrayList<>();
		
        for(int i = 0; i < trainsFe2Bo.length; i++ ){
        	
			System.out.println(" Ferrara --> -->  Bologna ");
			String getIdStz =  getJSON2(getStzId+"/"+trainsFe2Bo[i]);
			System.out.println(getIdStz);
			String [] r = getIdStz.split("-");
			if(r.length< 2) continue;
			String IdStz = r[2].trim();
            try{
			String ViaggiaTreno_resp = getJSON2(standardURL+"/"+IdStz+"/"+trainsFe2Bo[i]);
			
		    t_Re2Bo = getTrains2(ViaggiaTreno_resp);
		    m_R2B.put(trainsFe2Bo[i].toString(), t_Re2Bo);
            }catch(java.io.IOException e){
            	continue;
            }
		}
        
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate localDate = LocalDate.now();
		System.out.println(dtf.format(localDate)); //2016/11/16
		String date = dtf.format(localDate).toString();
		print("dataViaggiaTreno/B2F/V0.3_Data_collected_in_"+date+"_Bologna2Ferrara.tsv", m_B2R);
		print("dataViaggiaTreno/F2B/V0.4_Data_collected_in_"+date+"_Ferrara2Bologna.tsv", m_R2B);
		System.out.println(m_B2R.size()+"--> "+m_R2B.size());
	}
	
	private static String getJSON2(String urlString) throws IOException{ 
		URL url = new URL(urlString); 
		URLConnection conn = url.openConnection();  
		
		InputStream is = conn.getInputStream();
		
		String json = IOUtils.toString(is, "UTF-8");  
		is.close();    
		return json; 
	}  

	public static void print(String file, Map<String, List<Train>>m) throws Exception{  
		
		PrintWriter out = new PrintWriter(new FileWriter(new File(file)));  
		out.println("Nr_Treno\tStazione\tArrivo_Programmato\tArrivo_Effettivo\tRitardo_In_Minuti");
		for(String s : m.keySet()){ 
			
			for(int i = 0; i < m.get(s).size(); i++){
				out.println(s+"\t"+m.get(s).get(i).stazione+"\t"+convertTsp2Date2(m.get(s).get(i).orario)+"\t"
						+ ""+convertTsp2Date2(m.get(s).get(i).orarioEff)+"\t"+m.get(s).get(i).delay);
			}
			
		} 
		out.close(); 
	}
	
	public static List<Train> getTrains2( String resp){
		
		List<Train>trains = new ArrayList<Train>();
		
		String stazione = ""; 
		
		Long orario = null; 
		Long orarioEff = null; 
		 
		Train t = null;
		try { 

			JSONArray jarr = new JSONArray(resp);  
			
			for(int i = 0; i < jarr.length(); i++){ 
				
				Object jo = jarr.get(i); 
				System.out.println("jarr_i = "+jarr.get(i).toString());
				JSONObject jso = (JSONObject)jo;    
				JSONObject fermata = (JSONObject)jso.get("fermata");
              
                stazione = fermata.getString("stazione");
                orario = fermata.getLong("programmata");
                orarioEff = fermata.getLong("effettiva");
                double delay_in_min = (double)(orarioEff-orario)/1000/60;
                trains.add(new Train(stazione, orario, orarioEff, delay_in_min));
			} 
			
		}catch (JSONException e) {   
			System.err.println("Can't parse JSON string");   
			e.printStackTrace(); 
		}
	return trains;
	}
	
public static String convertTsp2Date2(long timestamp){
		
		Date data ;
		
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(timestamp);
		data = c.getTime();
		SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH.mm.ss");
		String datainstringa = new String(dateformat.format(data));
		return datainstringa;
		
	}
}

