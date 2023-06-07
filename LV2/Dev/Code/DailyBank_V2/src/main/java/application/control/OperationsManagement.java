package application.control;

import java.util.ArrayList;


import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * 
 * Cette classe permet de gérer les opérations pour un compte courant.
 * Elle contient des méthodes pour afficher une fenêtre de gestion des opérations,
 * enregistrer une opération de débit et récupérer la liste des opérations d'un compte courant. 
 *
 */
public class OperationsManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private OperationsManagementController omcViewController;
	private Client clientDuCompte;
	private CompteCourant compteConcerne;

	public OperationsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {

		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("operationsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des opérations");
			this.primaryStage.setResizable(false);

			this.omcViewController = loader.getController();
			this.omcViewController.initContext(this.primaryStage, this, _dbstate, client, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Affiche la fenêtre de gestion des opérations.
	 * 
	 */
	public void doOperationsManagementDialog() {
		this.omcViewController.displayDialog();
	}

	/**
	 * 
	 * Enregistre une opération de débit pour le compte courant.
	 * 
	 * @return l'opération enregistrée ou null si une erreur est survenue
	 * 
	 */
	public Operation enregistrerDebit() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.DEBIT);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();
				

				if(this.compteConcerne.estCloture.equals("N")) {
					if(oep.getTypeDebit() == true) {
						ao.insertDebitExceptionnel(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);
					} else {
						ao.insertDebit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);
					}
				}
				else {
					Alert alerteCptNonVide = new Alert(AlertType.WARNING);
	            	alerteCptNonVide.setHeaderText("Le compte est fermé");
	            	alerteCptNonVide.show();
				}

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}
	
	/**
	 * 
	 * Enregistre une opération de crédit pour le compte courant.
	 * 
	 * @return l'opération enregistrée ou null si une erreur est survenue
	 * 
	 */
	public Operation enregistrerCredit() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.CREDIT);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();
				
				if(this.compteConcerne.estCloture.equals("N")) {
					ao.insertCredit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);
				}
				else {
					Alert alerteCptNonVide = new Alert(AlertType.WARNING);
	            	alerteCptNonVide.setHeaderText("Le compte est fermé");
	            	alerteCptNonVide.show();
				}

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}
	
	/**
	 * 
	 * Enregistre une opération de crédit pour le compte courant.
	 * 
	 * @return l'opération enregistrée ou null si une erreur est survenue
	 * 
	 */
    public Operation enregistrerVirement() {
        // Ouvre et affiche une fenêtre d'édition d'opération
        // et récupère l'opération saisie
        OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
        Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.VIREMENT);
        // Vérifie si une opération a été saisie
        if (op != null) {
            try {
                Access_BD_Operation ao = new Access_BD_Operation();
                ao.insertVirement(this.compteConcerne.idNumCompte, op.idNumCompte, op.montant);
            } catch (DatabaseConnexionException e) {
                // gestion des exceptions de connexion à la BDD
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                op = null;
            } catch (ApplicationException ae) {
                // gestion des exceptions d'application
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                op = null;
            }
        }
        return op;
    }
	
	/**
	 * 
	 * Récupère la liste des opérations et le solde du compte courant.
	 * 
	 * @return un objet PairsOfValue contenant le compte courant et la liste des opérations
	 * 
	 */
	public PairsOfValue<CompteCourant, ArrayList<Operation>> operationsEtSoldeDunCompte() {
		ArrayList<Operation> listeOP = new ArrayList<>();

		try {
			// Relecture BD du solde du compte
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

			// lecture BD de la liste des opérations du compte de l'utilisateur
			Access_BD_Operation ao = new Access_BD_Operation();
			listeOP = ao.getOperations(this.compteConcerne.idNumCompte);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeOP = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeOP = new ArrayList<>();
		}
		System.out.println(this.compteConcerne.solde);
		return new PairsOfValue<>(this.compteConcerne, listeOP);
	}
	
}
