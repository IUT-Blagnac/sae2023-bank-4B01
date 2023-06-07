package application.view;

import java.io.PrintWriter;

import java.io.StringWriter;

import application.DailyBankState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.orm.exception.ApplicationException;

/**
 * 
 * Contrôleur pour la boîte de dialogue affichant les détails d'une exception.
 *
 */
public class ExceptionDialogController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private ApplicationException aException;

	// Manipulation de la fenêtre

	/**
	 * 
	 * Initialise le contexte du contrôleur.
	 * 
	 * @param _containingStage
	 * @param _dbstate
	 * @param _ae
	 */
	public void initContext(Stage _containingStage, DailyBankState _dbstate, ApplicationException _ae) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.aException = _ae;
		this.configure();
	}

	/**
	 * 
	 * Configure les éléments de la boîte de dialogue.
	 * 
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.lblTitre.setText(this.aException.getMessage());
		this.txtTable.setText(this.aException.getTableName().toString());
		this.txtOpe.setText(this.aException.getOrder().toString());
		this.txtException.setText(this.aException.getClass().getName());
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		this.aException.printStackTrace(pw);
		this.txtDetails.setText(sw.toString());
	}

	/**
	 * 
	 * Affiche la boîte de dialogue et attend que l'utilisateur la ferme.
	 * 
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 * 
	 * Gestionnaire d'événement pour la fermeture de la fenêtre.
	 * 
	 * @param e
	 * 
	 * @return null
	 * 
	 */
	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblTitre;
	@FXML
	private TextField txtTable;
	@FXML
	private TextField txtOpe;
	@FXML
	private TextField txtException;
	@FXML
	private TextArea txtDetails;

	/**
	 * 
	 * Ferme la fenêtre
	 * 
	 */
	@FXML
	private void doOK() {
		this.primaryStage.close();
	}
}
