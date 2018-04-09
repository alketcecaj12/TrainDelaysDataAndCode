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

public class GetTrainDelays5 {
	
	/*
	 * 
	 * 
	 * parsing dei treni da Bologna a Firenze e da Firenze a Bologna
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
		
		String [] trainsBo2Re = { "11521", "9501", "9503", "9507", "9509", "9561", "9513", "9521", "8413",
                                   "9419", "6557", "3055", "9425", "8427", "9529",
                                   "9427", "3067", "8419", "8415", "8451", "9575", 
                                   "9549","9451","3097", "9559", "6531",
                                   "6545", "9537", "9569", "9435", "9437", "9439", 
                                    "9545", "9443","9557", "599", "3083", "11633"}; 
		
		
		String [] trainsRe2Bo = {"11674", "6600", "11626", "35774", "3044", "9500", "9502", "9504","9508",
				                 "8508", "9566", "8408", "580",
                                 "9508", "9514", "9570", "8416", "9406", "9518", 
                                 "9572", "9414", "9520", "9532","9528", "9526", "8432", 
                                 "8434", "9544", "9578", "9536", "9540", "9430", 
                                 "9550", "9580","9446", "11634","9560", "9562", "9558" };
		
		List<Train>t_Bo2Re = new ArrayList<>();
		System.out.println("BoRe****************************");
		
		for(int i = 0; i < trainsBo2Re.length; i++ ){	
			
			System.out.println("Bologna --> Reggio Emilia ");	
			String getIdStz =  getJSON(getStzId+"/"+trainsBo2Re[i]);
		
			System.out.println("id stz "+getIdStz+ " for train "+trainsBo2Re[i]);
			String [] r = getIdStz.trim().split("-");
			if(r.length < 2) continue;
			//System.out.println("Ri = r0 "+r[0]+",r1 "+r[1]+",r2 "+r[2]);
			
			String IdStz = r[2];
			
			if(IdStz.length()>6){
				IdStz = IdStz.substring(0,6);
				System.out.println("inside if == "+IdStz);
			}
			System.out.println(standardURL+"/"+IdStz.trim()+"/"+trainsBo2Re[i].trim());
			try{
				String ViaggiaTreno_resp = getJSON(standardURL+"/"+IdStz.trim()+"/"+trainsBo2Re[i].trim());

				t_Bo2Re = getTrains(ViaggiaTreno_resp);
				m_B2R.put(trainsBo2Re[i].toString(), t_Bo2Re);
			
               }catch (Exception e){
			
		}
		}
		
		List<Train>t_Re2Bo = new ArrayList<>();
		
        for(int i = 0; i < trainsRe2Bo.length; i++ ){
        	
			System.out.println(" Reggio Emilia --> -->  Bologna ");
			String getIdStz =  getJSON(getStzId+"/"+trainsRe2Bo[i]);
			System.out.println(getIdStz);
			String [] r = getIdStz.split("-");
			if(r.length< 2) continue;
			String IdStz = r[2].trim();
            try{
			String ViaggiaTreno_resp = getJSON(standardURL+"/"+IdStz+"/"+trainsRe2Bo[i]);
			
		    t_Re2Bo = getTrains(ViaggiaTreno_resp);
		    m_R2B.put(trainsRe2Bo[i].toString(), t_Re2Bo);
            }catch(java.io.IOException e){
            	continue;
            }
		}
        
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate localDate = LocalDate.now();
		System.out.println(dtf.format(localDate)); //2016/11/16
		String date = dtf.format(localDate).toString();
		print("dataViaggiaTreno/B2FI/V0.3_Data_collected_in_"+date+"_Bologna2Firenze.tsv", m_B2R);
		print("dataViaggiaTreno/FI2B/V0.4_Data_collected_in_"+date+"_Firenze2Bologna.tsv", m_R2B);
		System.out.println(m_B2R.size()+"--> "+m_R2B.size());
	}
	
	private static String getJSON(String urlString) throws IOException{ 
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
				out.println(s+"\t"+m.get(s).get(i).stazione+"\t"+convertTsp2Date(m.get(s).get(i).orario)+"\t"
						+ ""+convertTsp2Date(m.get(s).get(i).orarioEff)+"\t"+m.get(s).get(i).delay);
			}
			
		} 
		out.close(); 
	}
	
	public static List<Train> getTrains( String resp){
		
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
	
public static String convertTsp2Date(long timestamp){
		
		Date data ;
		
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(timestamp);
		data = c.getTime();
		SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH.mm.ss");
		String datainstringa = new String(dateformat.format(data));
		return datainstringa;
		
	}
}