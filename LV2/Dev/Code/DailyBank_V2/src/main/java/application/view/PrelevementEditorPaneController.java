package application.view;


import java.util.ArrayList;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;

public class PrelevementEditorPaneController {

	// Etat application
	private DailyBankState dailyBankState;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Prelevement preEdite;
	private EditionMode editionMode;
	private Prelevement preResult;
	private Client client;
	private ObservableList<CompteCourant> oListCompteCourant;
	

	public void initContext(Stage _primaryStage, DailyBankState _dbstate, Client client) {
		this.primaryStage = _primaryStage;
		this.dailyBankState = _dbstate;
		this.client = client;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdPrelev;
	@FXML
	private TextField txtBeneficiaire;
	@FXML
	private TextField txtMontant;
	@FXML
	private TextField txtJourPrelev;
	@FXML
	private ComboBox<CompteCourant> cbComptes;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;


	/**
	 * 
	 * Permet d'afficher la boîte de dialogue des prélèvements.
	 * 
	 * @param prelevement 
	 * @param mode le mode d'édition (création, modification ou suppression)
	 * 
	 * @return le prélèvment résultant (null si annulé)
	 * 
	 */
	public Prelevement displayDialog(Prelevement prelevement, EditionMode mode) {
		
		this.editionMode = mode;
		
		if(prelevement == null) {
			this.preEdite = new Prelevement();
		}else {
			this.preEdite = new Prelevement(prelevement);
		}
		this.preResult = null;
		
		switch(mode) {
		
		case CREATION:
			
			this.txtIdPrelev.setDisable(true);
			this.txtBeneficiaire.setDisable(false);
			this.txtMontant.setDisable(false);
			this.txtJourPrelev.setDisable(false);
			this.cbComptes.setDisable(false);
			this.lblMessage.setText("Informations sur le nouveau prélèvement");
			this.btnOk.setText("Ajouter");
			this.btnCancel.setText("Annuler");
			break;
			
		case MODIFICATION:
			
			this.txtIdPrelev.setDisable(true);
			this.txtBeneficiaire.setDisable(true);
			this.txtMontant.setDisable(false);
			this.txtJourPrelev.setDisable(false);
			this.cbComptes.setDisable(false);
			this.lblMessage.setText("Informations prélèvement");
			this.btnOk.setText("Modifier");
			this.btnCancel.setText("Annuler");
			break;
			
		case SUPPRESSION:
			break;
		}

		// initialisation du contenu des champs
		this.txtIdPrelev.setText("" + this.preEdite.idPrelev);
		this.txtBeneficiaire.setText("" + this.preEdite.beneficiaire);
		this.txtMontant.setText("" + this.preEdite.montant);
		this.txtJourPrelev.setText("" + this.preEdite.dateRe);

		ComptesManagement comptes = new ComptesManagement(this.primaryStage, this.dailyBankState, this.client);
		ArrayList<CompteCourant> cpts = comptes.getComptesDunClient();
		this.oListCompteCourant = FXCollections.observableArrayList();
		oListCompteCourant.clear();
		for (CompteCourant cc : cpts) {
			if(cc.estCloture.equals("N")) {
				oListCompteCourant.add(cc);
			}
		}

		this.cbComptes.setItems(oListCompteCourant);

		this.preResult = null;

		this.primaryStage.showAndWait();

		return this.preResult;

	}
	
	/**
	 * 
	 * Annule l'action en cours et ferme la fenêtre.
	 * 
	 */
	@FXML
	private void doCancel() {
		this.preResult = null;
		this.primaryStage.close();
	}

	/**
	 * 
	 * Ajouter le prélèvement à la liste
	 * 
	 */
	@FXML
	private void doAjouter() {
		
		switch (this.editionMode) {
		
		case CREATION:
			if (this.isSaisieValide()) {
				this.preResult = this.preEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.preResult = this.preEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			break;
		}

	}

	/**
	 * 
	 * Permet de vérifier si la saisie de l'utilisateur est valide
	 * 
	 * @return vrai ou faux si c'est valide ou non
	 * 
	 */
	private boolean isSaisieValide() {
		
		this.preEdite.beneficiaire = this.txtBeneficiaire.getText().trim();
		
		if(isDouble(this.txtMontant)) {
			this.preEdite.montant = Double.parseDouble(this.txtMontant.getText().trim());
		}
		
		if(isNumber(this.txtJourPrelev)) {
			this.preEdite.dateRe = Integer.parseInt(this.txtJourPrelev.getText().trim());
		}
		
		if(this.cbComptes.getValue() != null) {
			this.preEdite.idNumCompte = this.cbComptes.getValue().idNumCompte;
		}
		
		if(this.preEdite.beneficiaire.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le bénéficiaire ne doit pas être vide",
					AlertType.WARNING);
			this.txtBeneficiaire.requestFocus();
			return false;
		}
		
		if(!(isDouble(this.txtMontant) && Double.parseDouble(this.txtMontant.getText().trim()) > 0)) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le montant doit nombre positif et non vide",
					AlertType.WARNING);
			this.txtMontant.requestFocus();
			this.txtMontant.setText("" + this.preEdite.montant);
			return false;
		}
		
		if(!(isNumber(this.txtJourPrelev) && Integer.parseInt(this.txtJourPrelev.getText().trim()) > 0 && Integer.parseInt(this.txtJourPrelev.getText().trim()) <= 28)) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le jour doit être entre 1 et 28",
					AlertType.WARNING);
			this.txtJourPrelev.requestFocus();
			this.txtJourPrelev.setText("" + this.preEdite.dateRe);
			return false;
		}
		
		if(this.cbComptes.getValue() == null) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Un compte doît être sélectionné",
					AlertType.WARNING);
			this.cbComptes.requestFocus();
			return false;
		}
		
		return true;
	}
	

	private boolean isNumber(TextField message) {
		try {
			Integer.parseInt(message.getText());
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean isDouble(TextField message) {
		try {
			Double.parseDouble(message.getText());
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

}
