package datacollection;

public class Train {
	
	public String stazione ; 
	public Long orario;
	public Long orarioEff;
    public double delay;


	public Train(String s, long o, long oe, double d){
		
		this.stazione = s; 
		
		this.orario = o; 
		
		this.orarioEff = oe; 
		this.delay = d;
		
	}
	
	public String toString(){
		return stazione+","+orario+","+orarioEff+","+delay;
	}
	
	
}
