package application.view;


import java.util.ArrayList;
import java.util.Optional;
import application.DailyBankState;
import application.control.PrelevementManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.Prelevement;

public class PrelevementManagementController {

	@SuppressWarnings("unused")
	private DailyBankState dailyBankState;
	
	private PrelevementManagement pmDialogController;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	
	private ObservableList<Prelevement> oListPrelevement;

	/**
	 * 
	 * Initialise les paramètres de la fenêtre
	 * 
	 * @param _primaryStage
	 * @param _pm
	 * @param _dbstate
	 * @param client
	 * 
	 */
	public void initContext(Stage _primaryStage, PrelevementManagement _pm, DailyBankState _dbstate, Client client) {
		this.primaryStage = _primaryStage;
		this.dailyBankState = _dbstate;
		this.pmDialogController = _pm;
		this.clientDesComptes = client;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListPrelevement = FXCollections.observableArrayList();
		this.lvPrelevements.setItems(this.oListPrelevement);
		this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelevements.getFocusModel().focus(-1);
		this.lvPrelevements.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

	/**
	 * 
	 * Affiche la fenêtre en mode modal.
	 * 
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}
	

	@FXML
	private TextField txtNum;
	@FXML
	private ListView<Prelevement> lvPrelevements;
	@FXML
	private Button btnModifPrelev;
	@FXML
	private Button btnSuppPrelev;
	@FXML
	private Button btnCreerPrelev;
	@FXML
	private Button butRecherche;
	@FXML
	private Button butCancel;

	@FXML
	private void doRechercher() {
		int numCompte;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					this.txtNum.setText("");
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			numCompte = -1;
		}

		ArrayList<Prelevement> listePre;
		listePre = this.pmDialogController.getlistePrelevements(this.clientDesComptes.idNumCli , numCompte);

		this.oListPrelevement.clear();
		for (Prelevement prel : listePre) {
			this.oListPrelevement.add(prel);
		}

		this.validateComponentState();
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * 
	 * Supprime le prélèvement sélectionné
	 * 
	 */
	@FXML
	private void doSupprimerPrelevement() {
		
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		
		if (selectedIndice >= 0) {
			
			Prelevement pre = this.oListPrelevement.get(selectedIndice);
			Alert dialog = new Alert(AlertType.CONFIRMATION);
			dialog.setTitle("Confirmation");
			dialog.setContentText("Voulez-vous vraiment supprimer le prélèvement ?");
			dialog.setHeaderText("Supprimer le prélèvement ?");
			dialog.initOwner(this.primaryStage);
			Optional<ButtonType> reponse = dialog.showAndWait();
			
			if (reponse.get() == ButtonType.OK) {
				
				this.pmDialogController.supprimerPrelevement(pre);
				doRechercher();
				
			}
		}
	}

	/**
	 * 
	 * Permet de créer un nouveau prélèvement sur un compte
	 * 
	 */
	@FXML
	private void doNouveauPrelevement() {
		
		Prelevement prelevement;
		prelevement = this.pmDialogController.nouveauPrelevement(this.clientDesComptes);
		
		if (prelevement != null) {
			
			this.oListPrelevement.add(prelevement);
			doRechercher();
			
		}
	}
	
	/**
	 * 
	 * Permet de modifier le prélèvement sélectionné
	 * 
	 */
	@FXML
	private void doModifierPrelevement() {
		
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		
		if (selectedIndice >= 0) {
			
			Prelevement prelevement = this.pmDialogController.modifierPrelevement(this.clientDesComptes, this.oListPrelevement.get(selectedIndice));
			
			if(prelevement != null) {
				
				this.oListPrelevement.set(selectedIndice, prelevement);
				doRechercher();
			}
		}
	}


	private void validateComponentState() {
		
		this.btnSuppPrelev.setDisable(true);
		
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		
		if (selectedIndice >= 0) {
			
			this.btnModifPrelev.setDisable(false);
			this.btnSuppPrelev.setDisable(false);
			
		} else {
			
			this.btnModifPrelev.setDisable(true);
			this.btnSuppPrelev.setDisable(true);
		}
	}
}