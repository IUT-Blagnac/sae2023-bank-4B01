package application.view;

import application.DailyBankState;
import application.control.ClientsManagement;
import application.control.EmpruntManagement;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.data.Employe;
import model.data.Emprunt;

public class EmpruntManagementController {
	
	
	
	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ClientsManagementController
	private EmpruntManagement emDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;
	
	
	
	
	/**
	 * 
	 * Initialise les paramètres de la fenêtre (contenant de la scène, contrôleur du dialogue et état courant de l'application).
	 * 
	 * @param _containingStage la fenêtre physique contenant la scène associée à ce contrôleur
	 * @param _cm le contrôleur du dialogue associé à cette fenêtre
	 * @param _dbstate l'état courant de l'application
	 * 
	 */
	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, EmpruntManagement _em, DailyBankState _dbstate) {
		this.emDialogController = _em;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
	}
	
	/**
	 * 
	 * Affiche la fenêtre en mode modal.
	 * 
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}
	
	
	
	
	@FXML
	private TextField montant;
	@FXML
	private TextField duree;
	@FXML
	private TextField taux;
	@FXML
	private TextField applicable;
	@FXML
	private TextField tauxAssurance;
	@FXML
	private RadioButton mois;
	@FXML
	private RadioButton annee;
	@FXML
	private RadioButton oui;	
	@FXML
	private RadioButton non;
	@FXML
	private Button valider;
	@FXML
	private Button annuler;
	
	
	private boolean isSimAssurance;
	
	
	/**
	 * 
	 * Annule l'action en cours et ferme la fenêtre.
	 * 
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
	
	
	/**
	 * 
	 * Ouvre la fenetre des résultats si tous les
	 * champs sont corrects
	 * 
	 */
	@FXML
	private void doValider() {
		if (estValide()) {
					
			this.emDialogController.resultatEmprunt();
			
		}
	}

	


	/**
	 * 
	 * Active la possibilité de remplir le champ
	 * taux d'assurance
	 * 
	 */
	@FXML
	private void assurerOui() {
		tauxAssurance.setDisable(false);
		isSimAssurance=true;
	}
	
	/**
	 * 
	 * Désactive la possibilité de remplir le champ
	 * taux d'assurance
	 * 
	 */
	@FXML
	private void assurerNon() {
		tauxAssurance.setDisable(true);
		tauxAssurance.setText("");
		isSimAssurance=false;
	}

	
	
	
	
	
	
	/**
	 * vérifie si tous les champs  sont valides
	 * - des réels
	 * - positifs
	 * - non-vides
	 * @return true (si tout est valide)
	 */
	private boolean estValide() {
	      
		try {
	          // Ici traitement si c'est un float
	    	  float myFloat = Float.parseFloat(montant.getText());
	          myFloat = Float.parseFloat(duree.getText());
	          myFloat = Float.parseFloat(taux.getText());
	          myFloat = Float.parseFloat(applicable.getText());
	          if(isSimAssurance) { myFloat = Float.parseFloat(tauxAssurance.getText()); }
	          

	        } catch (NumberFormatException ex) {
	          // Ici traitement si ce n'est pas un float
	        	System.out.println("Erreur: un ou plusieurs champ(s) sont incorrects !");
	        	return false;
	        }
		return true;
	}
}
