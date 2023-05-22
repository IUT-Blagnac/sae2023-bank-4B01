package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.EmployeManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

public class EmployeManagementController {
	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à EmployeManagementController
	private EmployeManagement emDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> oListEmploye;
	
	// Acces bd
	private Access_BD_Employe accessBD;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, EmployeManagement _em, DailyBankState _dbstate) {
		this.emDialogController = _em;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListEmploye = FXCollections.observableArrayList();
		this.listeEm.setItems(this.oListEmploye);
		this.listeEm.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.listeEm.getFocusModel().focus(-1);
		this.listeEm.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

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
	private ListView<Employe> listeEm;
	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private Button butRecherche;
	@FXML
	private Button butModifier;
	@FXML
	private Button butSupprimer;
	
	
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

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (numCompte != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}

		// Recherche des clients en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les clients
		ArrayList<Employe> listeEmp;
		listeEmp = this.emDialogController.getlisteEmploye(numCompte, debutNom, debutPrenom);

		this.oListEmploye.clear();
		this.oListEmploye.addAll(listeEmp);
		this.validateComponentState();
	}
	
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
	
	
	@FXML
	private void doModifierEmploye() {

		int selectedIndice = this.listeEm.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe empMod = this.oListEmploye.get(selectedIndice);
			Employe result = this.emDialogController.modifierEmploye(empMod);
			if (result != null) {
				this.oListEmploye.set(selectedIndice, result);
			}
		}
	}
	
	@FXML
	private void doCreerEmploye() {
		Employe employe;
		employe=this.emDialogController.nouveauEmploye();
			if (employe != null) {
				this.oListEmploye.add(employe);
			}
	}
	
	@FXML
	private void doSupprimerEmploye() throws DataAccessException, DatabaseConnexionException {

		int selectedIndice = this.listeEm.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe empMod = this.oListEmploye.get(selectedIndice);
			this.emDialogController.supprimerEmploye(empMod);
		}
	}
	
	
	private void validateComponentState() {
        // Non implémenté => désactivé
        int selectedIndice = this.listeEm.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            Employe EmplMod = this.oListEmploye.get(selectedIndice);
            if (EmplMod.droitsAccess.equals("chefAgence")){
                this.butModifier.setDisable(true);
                this.butSupprimer.setDisable(true);
            }
            else {
                this.butModifier.setDisable(false);
                this.butSupprimer.setDisable(false);
            }
        } else {
            this.butModifier.setDisable(true);
            this.butSupprimer.setDisable(true);
        }
    }
	


}
