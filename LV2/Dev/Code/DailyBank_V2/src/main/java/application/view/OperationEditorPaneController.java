package application.view;

import java.util.Locale;


import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;

/**
 * 
 * Cette classe contrôle le fichier FXML de l'interface utilisateur pour ajouter des opérations.
 *
 */
public class OperationEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation operationResultat;
	private boolean debitExceptionnel = false;

	/**
	 * 
	 * Initialise le contexte du contrôleur.
	 * 
	 * @param _containingStage
	 * @param _dbstate
	 */
	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	/**
	 * 
	 * Configure le contrôleur
	 * 
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}
	
	
	/**
	 * Précise si il s'agit d'un débit exceptionnel
	 */
	public boolean isDebitExceptionnel() {
		return this.debitExceptionnel;
	}
	
	

	/**
	 * 
	 * Affiche la boîte de dialogue pour ajouter une opération
	 * 
	 * @param cpte
	 * @param mode
	 * 
	 * @return l'opération ajoutée ou null si l'utilisateur a annulé l'opération
	 * 
	 */
	public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;

		switch (mode) {
		case DEBIT:

			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			this.btnOk.setText("Effectuer Débit");
			this.btnCancel.setText("Annuler débit");

			ObservableList<String> listTypesOpesPossibles = FXCollections.observableArrayList();
			listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_DEBIT_GUICHET);

			this.cbTypeOpe.setItems(listTypesOpesPossibles);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		case CREDIT:
			
			String infoCre = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(infoCre);

			this.btnOk.setText("Effectuer Crédit");
			this.btnCancel.setText("Annuler Crédit");

			ObservableList<String> ListTypesOpesPossibles = FXCollections.observableArrayList();
			ListTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_CREDIT_GUICHET);

			this.cbTypeOpe.setItems(ListTypesOpesPossibles);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		case VIREMENT:
			
			String infoVir = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(infoVir);
			this.txtVirement = new TextField();
			this.labVirement = new Label("N° Destinataire : ");
			gPVirement.add(txtVirement, 1, 1);
			gPVirement.add(labVirement, 0, 1);
			this.btnOk.setText("Effectuer Virement");
			this.btnCancel.setText("Annuler");
			this.cbTypeOpe.setDisable(true);
			this.operationResultat.idNumCompte=Integer.parseInt(this.txtVirement.getText());
			this.primaryStage.showAndWait();
			
			break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		this.operationResultat = null;
		this.cbTypeOpe.requestFocus();

		this.primaryStage.showAndWait();
		return this.operationResultat;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblMessage;
	@FXML
	private Label lblMontant;
	@FXML
	private ComboBox<String> cbTypeOpe;
	@FXML
	private TextField txtMontant;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;
	@FXML
	private TextField txtVirement;
	@FXML
	private GridPane gPVirement;
	@FXML
	private Label labVirement;

	/**
	 * 
	 * Annule l'opération en cours d'ajout et ferme la boîte de dialogue.
	 * 
	 */
	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}

	/**
	 * 
	 * Ajoute l'opération en cours d'ajout et ferme la boîte de dialogue. 
	 * Pour les débits, vérifie que le montant est valide et ne dépasse pas le découvert autorisé du compte. 
	 * Si le montant n'est pas valide, un message d'erreur est affiché et l'opération n'est pas ajoutée.
	 * 
	 */
	@FXML
	private void doAjouter() {
		switch (this.categorieOperation) {
		case DEBIT:
			// règles de validation d'un débit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
			double montant;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
				info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
						+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
						+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				this.lblMessage.setText(info);
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.lblMessage.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				
				
				boolean rep = AlertUtilities.confirmYesCancel(primaryStage, "Découvert dépassé !", "Voulez-vous confirmer le débit exceptionnel ?", null, AlertType.CONFIRMATION);
				
				if(rep == true) {
					if(this.dailyBankState.isChefDAgence()) {
						this.debitExceptionnel = true;
					}
					else {
						AlertUtilities.showAlert(primaryStage, "Erreur : action impossible", "Vous ne pouvez pas effectuer cette action : vous n'êtes pas chef d'agence", null, AlertType.ERROR);
						this.debitExceptionnel = false;
					}
				}else {
					return;
				}
			}
			String typeOp = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
			this.primaryStage.close();
			break;
		case CREDIT:
			
			double montantCre;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			String infoCre = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(infoCre);

			try {
				montantCre = Double.parseDouble(this.txtMontant.getText().trim());
				if (montantCre <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			String TypeOp = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montantCre, null, null, this.compteEdite.idNumCli, TypeOp);
			this.primaryStage.close();
			break;
		case VIREMENT:
			
			double montantVir;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			String infoVir = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(infoVir);

			try {
				montantVir = Double.parseDouble(this.txtMontant.getText().trim());
				if (montantVir <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
		
				return;
			}
			String TypeOpV = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montantVir, null, null, this.compteEdite.idNumCli, TypeOpV);
			this.primaryStage.close();
			break;
		}
	}
}
