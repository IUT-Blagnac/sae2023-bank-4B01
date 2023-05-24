package application.view;

import application.DailyBankState;
import application.control.ResultatEmpruntPane;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.data.Emprunt;

public class ResultatEmpruntPaneController {
	// Etat courant de l'application
	private DailyBankState dailyBankState;

	private ResultatEmpruntPane resultatemprunt;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;
	
	private ResultatEmpruntPaneController repViewController;
	
	
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
		this.resultatemprunt = _rep;
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
		this.repViewController.tableau.setEditable(false);
		TableColumn<Emprunt, String> periode = new TableColumn<Emprunt, String>("Nombre période");
		periode.setPrefWidth(150);
		TableColumn<Emprunt, String> capitalDebut = new TableColumn<Emprunt, String>("Capital début période");
		capitalDebut.setPrefWidth(250);
		TableColumn<Emprunt, String> interet = new TableColumn<Emprunt, String>("Interêt");
		interet.setPrefWidth(150);
		TableColumn<Emprunt, String> principal = new TableColumn<Emprunt, String>("Principal");
		principal.setPrefWidth(150);
		TableColumn<Emprunt, String> mensualite = new TableColumn<Emprunt, String>("Mensualité");
		mensualite.setPrefWidth(150);
		TableColumn<Emprunt, String> capitalFin = new TableColumn<Emprunt, String>("Capital fin période");
		capitalFin.setPrefWidth(250);
		this.repViewController.tableau.getColumns().addAll(periode, capitalDebut, interet, principal, mensualite, capitalFin);
		
		periode.setCellValueFactory(new PropertyValueFactory<>(this.repViewController.toString()));
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
