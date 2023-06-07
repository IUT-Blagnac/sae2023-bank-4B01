package model.data;

import java.text.DecimalFormat;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Emprunt {
	private SimpleIntegerProperty numPeriode;
	private SimpleDoubleProperty capitalDebut;
	private SimpleDoubleProperty interet;
	private SimpleDoubleProperty principal;
	private SimpleDoubleProperty mensualite;
	private SimpleDoubleProperty capitalFin;
	
	public Emprunt(int numPeriode, double capitalDebut, double interet, double principal, double mensualite, double capitalFin) {
		
		
		this.numPeriode = new SimpleIntegerProperty(numPeriode);
		this.capitalDebut = new SimpleDoubleProperty(capitalDebut);
		this.interet = new SimpleDoubleProperty(interet);
		this.principal = new SimpleDoubleProperty(principal);
		this.mensualite = new SimpleDoubleProperty(mensualite);
		this.capitalFin = new SimpleDoubleProperty(capitalFin);
	}

	public int getNumPeriode() {
		return numPeriode.get();
	}


	public double getCapitalDebut() {
		return capitalDebut.get();
	}



	public double getInteret() {
		return interet.get();
	}


	public double getPrincipal() {
		return principal.get();
	}



	public double getMensualite() {
		return mensualite.get();
	}



	public double getCapitalFin() {
		return capitalFin.get();
	}


	
	
	
	
}
