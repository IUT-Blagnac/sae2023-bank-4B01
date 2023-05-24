package application.view;

import application.DailyBankState;
import application.control.ResultatEmpruntPane;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.data.Emprunt;

public class ResultatEmpruntPaneController {
	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ClientsManagementController
	private ResultatEmpruntPane repDialogController;

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
	public void initContext(Stage _containingStage, ResultatEmpruntPane _rep, DailyBankState _dbstate) {
		this.repDialogController = _rep;
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
	public TableView<Emprunt> tableau;
	
	
	/**
	 * 
	 * Annule l'action en cours et ferme la fenêtre.
	 * 
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
}
