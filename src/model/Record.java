package model;

public class Record {
	
	private String nrtreno; 
	private String stz;
	private String arrivo_programmato; 
	private String arrivo_effettivo; 
	private double ritardo; 
	
	
	
	public Record(String nrtreno, String st, String arrivo_p, String arrivo_e, double rit){
		this.nrtreno = nrtreno; 
		this.stz = st; 
		this.arrivo_programmato = arrivo_p;
		this.arrivo_effettivo = arrivo_e; 
		this.ritardo = rit; 
	}



	public String getNrtreno() {
		return nrtreno;
	}



	public void setNrtreno(String nrtreno) {
		this.nrtreno = nrtreno;
	}



	public String getStz() {
		return stz;
	}



	public void setStz(String stz) {
		this.stz = stz;
	}



	public String getArrivo_programmato() {
		return arrivo_programmato;
	}



	public void setArrivo_programmato(String arrivo_programmato) {
		this.arrivo_programmato = arrivo_programmato;
	}



	public String getArrivo_effettivo() {
		return arrivo_effettivo;
	}



	public void setArrivo_effettivo(String arrivo_effettivo) {
		this.arrivo_effettivo = arrivo_effettivo;
	}



	public double getRitardo() {
		return ritardo;
	}



	public void setRitardo(double ritardo) {
		this.ritardo = ritardo;
	}

	
	
	
}
