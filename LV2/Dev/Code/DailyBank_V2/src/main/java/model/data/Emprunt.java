package model.data;

public class Emprunt {
	public int numPeriode;
	public double capitalDebut;
	public double interet;
	public double principal;
	public double mensualite;
	public double capitalFin;
	
	public Emprunt(int numPeriode, double capitalDebut, double interet, double principal, double mensualite) {
		this.numPeriode = numPeriode;
		this.capitalDebut = capitalDebut;
		this.interet = interet;
		this.principal = principal;
		this.mensualite = mensualite;
	}
	
	
}
