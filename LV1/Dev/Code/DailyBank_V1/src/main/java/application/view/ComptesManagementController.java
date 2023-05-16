package application.view;

import java.util.ArrayList;


import application.DailyBankState;
import application.control.CompteEditorPane;
import application.control.ComptesManagement;
import application.tools.EditionMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

/**
 * 
 * Cette classe est le contrôleur de la fenêtre de gestion des comptes d'un client.
 *
 */
public class ComptesManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ComptesManagementController
	private ComptesManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> oListCompteCourant;

	/**
	 * 
	 * Initialise le contexte de la fenêtre
	 * 
	 * @param _containingStage
	 * @param _cm
	 * @param _dbstate
	 * @param client
	 * 
	 */
	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	/**
	 * 
	 * Configure la fenêtre et initialise les données.
	 * 
	 */
	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}

	/**
	 * 
	 * Affiche la fenêtre de dialogue.
	 * 
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 * 
	 * Ferme la fenêtre de gestion des comptes.
	 * 
	 * @param e
	 * @return null
	 */
	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;

	/**
	 * 
	 * Ferme la fenêtre de gestion des comptes.
	 * 
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * 
	 * Ouvre la fenêtre de gestion des opérations pour le compte sélectionné
	 * 
	 */
	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			if(cpt.estCloture.equals("N")){
				this.cmDialogController.gererOperationsDUnCompte(cpt);
			}
		}
		this.loadList();
		this.validateComponentState();
	}

	/**
	 * 
	 * Modifie le compte sélectionné
	 * 
	 */
	@FXML
	private void doModifierCompte() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cptMod = this.oListCompteCourant.get(selectedIndice);
			CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
			CompteCourant result = cep.doCompteEditorDialog(this.clientDesComptes, cptMod, EditionMode.MODIFICATION);
			if (result != null && !result.equals(cptMod) && result.idNumCompte == cptMod.idNumCompte) {
				this.cmDialogController.modifierCompte(cptMod);
				this.loadList();
			}
		}
        this.loadList();
        this.validateComponentState();
	}
	

	
	/**
	 * 
	 * Cree un nouveau comptz
	 * 
	 */
	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cmDialogController.creerNouveauCompte();
		if (compte != null) {
			this.oListCompteCourant.add(compte);
		}
	}
	
	
	/**
     * 
     * Supprime le compte sélectionné.
     * 
     */
    @FXML
    private void doSupprimerCompte() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
            if(cpt.solde == 0) {
                this.cmDialogController.cloturerCompte(cpt);
            }
            else {
            	Alert alerteCptNonVide = new Alert(AlertType.WARNING);
            	alerteCptNonVide.setHeaderText("Le solde du compte n'est pas vide");
            	alerteCptNonVide.show();
            }
        }
        this.loadList();
        this.validateComponentState();
    }

    
	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cmDialogController.getComptesDunClient();
		this.oListCompteCourant.clear();
		this.oListCompteCourant.addAll(listeCpt);
	}

	private void validateComponentState() {
		this.btnModifierCompte.setDisable(false);
		this.btnSupprCompte.setDisable(false);

		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnVoirOpes.setDisable(false);
		} else {
			this.btnVoirOpes.setDisable(true);
		}
	}
}
