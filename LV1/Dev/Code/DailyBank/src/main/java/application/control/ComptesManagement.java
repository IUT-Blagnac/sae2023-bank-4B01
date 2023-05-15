package application.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;


import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.LogToDatabase;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

/**
 * 
 * Cette classe est responsable de la gestion de la fenêtre de gestion des comptes d'un client. 
 * Elle permet d'afficher la liste des comptes d'un client, 
 * de créer un nouveau compte et 
 * de gérer les opérations d'un compte sélectionné.
 *
 */
public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmcViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;

	/**
	 * 
	 * Constructeur de la classe ComptesManagement.
	 * 
	 * @param _parentStage Le stage parent de la fenêtre de gestion des comptes.
	 * @param _dbstate Le DailyBankState de l'application.
	 * @param client Le client dont on souhaite afficher les comptes.
	 * 
	 */
	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);

			this.cmcViewController = loader.getController();
			this.cmcViewController.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Affiche la fenêtre de gestion des comptes
	 * 
	 */
	public void doComptesManagementDialog() {
		this.cmcViewController.displayDialog();
	}
	

	/**
	 * 
	 * Ouvre la fenêtre de gestion des opérations pour un compte donné.
	 * 
	 * @param cpt Le compte dont on souhaite gérer les opérations.
	 * 
	 */
	public void gererOperationsDUnCompte(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dailyBankState,
				this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	/**
	 * 
	 * Ouvre la fenêtre d'édition d'un nouveau compte et l'ajoute à la liste des comptes du client.
	 * 
	 * @return Le compte créé, ou null si l'utilisateur annule la création
	 * 
	 */
	public CompteCourant creerNouveauCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
		if (compte != null) {
			try {				
				Access_BD_CompteCourant acces = new Access_BD_CompteCourant();
				acces.creerCompte(compte);
				
				// if JAMAIS vrai
				// existe pour compiler les catchs dessous
//				if (Math.random() < -1) {
//					throw new ApplicationException(Table.CompteCourant, Order.INSERT, "todo : test exceptions", null);
//				}
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
		return compte;
	}

	/**
	 * 
	 * Récupère la liste des comptes d'un client depuis la base de données.
	 * 
	 * @return La liste des comptes du client, ou une liste vide en cas d'erreur.
	 * 
	 */
	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}
}
