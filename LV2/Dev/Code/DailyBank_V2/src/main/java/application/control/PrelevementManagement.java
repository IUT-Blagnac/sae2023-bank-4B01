
package application.control;

import java.util.ArrayList;


import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.PrelevementManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Prelevement;
import model.orm.Access_BD_Prelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class PrelevementManagement {
	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private PrelevementManagementController pmcViewController;
	private Client clientDuCompte;

	/**
	 * Constructeur de la classe PrelevementManagement
	 * 
	 * @param _parentStage La fenêtre parente
	 * @param _dbstate La banque
	 * @param client Le client
	 * 
	 */
	public PrelevementManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDuCompte = client;
		this.dailyBankState = _dbstate;
		
		try {
			
			FXMLLoader loader = new FXMLLoader( PrelevementManagementController.class.getResource("prelevementmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+100, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des prélèvements");
			this.primaryStage.setResizable(false);

			this.pmcViewController = loader.getController();
			this.pmcViewController.initContext(this.primaryStage, this, this.dailyBankState, this.clientDuCompte);

		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
	}


	 /**
      *
	  * Affiche la fenêtre de gestion des prélèvements
	  *
	 */
	public void doPrelevementsManagementDialog() {
		
		this.pmcViewController.displayDialog();
		
	}
	

	/**
	 * 
	 * Récupère la liste des prélèvements correspondant aux critères de recherche
	 * 
	 * @param _numCli le numéro de compte à rechercher
	 * @param _numCompte le début du nom à rechercher
	 * 
	 * @return la liste des prélèvements correspondants
	 * 
	 */
	public ArrayList<Prelevement> getlistePrelevements(int numCli, int numCompte) {
		
		ArrayList<Prelevement> listePre = new ArrayList<>();
		
		try {
			
			Access_BD_Prelevement ap = new Access_BD_Prelevement();
			listePre = ap.getPrelevements(numCli, numCompte);
			
		} catch (DatabaseConnexionException e) {
			
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listePre = new ArrayList<>();
			
		} catch (ApplicationException ae) {
			
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listePre = new ArrayList<>();
			
		}
		
		return listePre;
		
	}


	/**
	 * 
	 * Supprime le prélèvement
	 * 
	 * @param pre Le prélèvement
	 * 
	 */
	public void supprimerPrelevement(Prelevement pre) {
		
		if (pre != null) {
			
			try {
				Access_BD_Prelevement ap = new Access_BD_Prelevement();
				ap.supprimerPrelevement(pre);

				if (Math.random() < -1) {
					throw new ApplicationException(Table.PrelevementAutomatique, Order.DELETE, "todo : test exceptions", null);
				}
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
	}


	/**
	 * 
	 * Crée un nouveau prélèvement au client
	 * 
	 * @param client Le client
	 * 
	 * @return Le nouveau prélèvement
	 * 
	 */
	public Prelevement nouveauPrelevement(Client client) {
		
		Prelevement prelevement;
		PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState, client);
		
		prelevement = pep.doPrelevementEditorDialog(null, EditionMode.CREATION);
		
		if(prelevement != null){
			try {
				Access_BD_Prelevement ap = new Access_BD_Prelevement();

				ap.insertPrelevement(prelevement);
				
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				prelevement = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				prelevement = null;
			}
		}
		return prelevement;
	}

	/**
	 * 
	 * Modifie le prélèvement du client
	 * 
	 * @param client Le client
	 * 
	 * @return Le prélèvement modifié
	 * 
	 */
	public Prelevement modifierPrelevement(Client client, Prelevement prelevementActuel) {
		
		Prelevement prelevement;
		PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState, client);
		
		prelevement = pep.doPrelevementEditorDialog(prelevementActuel, EditionMode.MODIFICATION);
		
		if(prelevement != null){
			try {
				
				Access_BD_Prelevement ap = new Access_BD_Prelevement();

				ap.updatePrelevement(prelevement);
				
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				prelevement = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				prelevement = null;
			}
		}
		return prelevement;
	}
	
}