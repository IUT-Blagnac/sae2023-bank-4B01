package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmployeManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Employe;
import model.orm.Access_BD_Client;
import model.orm.Access_BD_Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployeManagement {
	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private EmployeManagementController emViewController;

	public EmployeManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmployeManagementController.class.getResource("employemanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des employées");
			this.primaryStage.setResizable(false);

			this.emViewController = loader.getController();
			this.emViewController.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doEmployeManagementDialog() {
		this.emViewController.displayDialog();
	}
	
	public ArrayList<Employe> getlisteEmploye(int numEmploye, String debutNom, String debutPrenom) {
		ArrayList<Employe> listeEmp = new ArrayList<>();
		try {
			// Recherche des clients en BD. cf. AccessClient > getClients(.)
			// numCompte != -1 => recherche sur numCompte
			// numCompte == -1 et debutNom non vide => recherche nom/prenom
			// numCompte == -1 et debutNom vide => recherche tous les clients

			Access_BD_Employe ac = new Access_BD_Employe();
			listeEmp = ac.getListeEmploye(this.dailyBankState.getEmployeActuel().idAg, numEmploye, debutNom, debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeEmp = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeEmp = new ArrayList<>();
		}
		return listeEmp;
	}
	
	
	public Employe modifierEmploye(Employe em) {
		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
		Employe result = eep.doEmployeEditorDialog(em, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				Access_BD_Employe ae = new Access_BD_Employe();
				ae.updateEmploye(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}
	
//	public Employe supprimerEmploye(Employe em) {
//		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
//		Employe result = eep.doEmployeEditorDialog(em, EditionMode.SUPPRESSION);
//		if (result != null) {
//			try {
//				Access_BD_Employe ae = new Access_BD_Employe();
//				ae.deleteEmploye(result);
//			} catch (DatabaseConnexionException e) {
//				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
//				ed.doExceptionDialog();
//				result = null;
//				this.primaryStage.close();
//			} catch (ApplicationException ae) {
//				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
//				ed.doExceptionDialog();
//				result = null;
//			}
//		}
//		return result;
//	}

	public Employe nouveauEmploye() {
		Employe employe;
        EmployeEditorPane cep = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
        employe = cep.doEmployeEditorDialog(null, EditionMode.CREATION);
        if (employe != null) {
            try {
                Access_BD_Employe ac = new Access_BD_Employe();

                ac.insertEmploye(employe);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                employe = null;
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                employe = null;
            }
        }
        return employe;
	}
}
