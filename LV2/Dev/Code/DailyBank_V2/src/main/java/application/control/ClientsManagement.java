package application.control;

import java.util.ArrayList;


import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
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

/**
 * 
 * Cette classe permet de gérer la fenêtre de gestion des clients de l'application
 *
 */
public class ClientsManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private ClientsManagementController cmcViewController;

	
	/**
	 * Constructeur de la classe ClientsManagement
	 * 
	 * @param _parentStage Stage parent de la fenêtre
	 * @param _dbstate Etat courant de l'application
	 * 
	 */
	public ClientsManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("clientsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des clients");
			this.primaryStage.setResizable(false);

			this.cmcViewController = loader.getController();
			this.cmcViewController.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 /**
      *
	  * Affiche la fenêtre de gestion des clients
	  *
	 */
	public void doClientManagementDialog() {
		this.cmcViewController.displayDialog();
	}

	
	/**
	 * 
	 * Ouvre la fenêtre d'édition d'un client en mode modification
	 * 
	 * @param c le client à modifier
	 * @return le client modifié ou null si l'opération a échoué
	 * 
	 */
	public Client modifierClient(Client c) {
		ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dailyBankState);
		Client result = cep.doClientEditorDialog(c, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				Access_BD_Client ac = new Access_BD_Client();
				ac.updateClient(result);
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
	
	/**
	 * 
	 * Ouvre la fenêtre d'édition d'un nouveau client en mode création
	 * 
	 * @return le client créé ou null si l'opération a échoué
	 * 
	 */
	public Client nouveauClient() {
		Client client;
		ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dailyBankState);
		client = cep.doClientEditorDialog(null, EditionMode.CREATION);
		if (client != null) {
			try {
				Access_BD_Client ac = new Access_BD_Client();

				ac.insertClient(client);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				client = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				client = null;
			}
		}
		return client;
	}

	/**
	 * 
	 * Ouvre la fenêtre de gestion des comptes d'un client
	 * 
	 * @param c le client pour lequel gérer les comptes
	 * 
	 */
	public void gererComptesClient(Client c) {
		ComptesManagement cm = new ComptesManagement(this.primaryStage, this.dailyBankState, c);
		cm.doComptesManagementDialog();
	}
	
	/**
	 * 
	 * Récupère la liste des clients correspondant aux critères de recherche
	 * 
	 * @param _numCompte le numéro de compte à rechercher
	 * @param _debutNom le début du nom à rechercher
	 * @param _debutPrenom le début du prénom à rechercher
	 * 
	 * @return la liste des clients correspondants
	 * 
	 */
	public ArrayList<Client> getlisteComptes(int _numCompte, String _debutNom, String _debutPrenom) {
		ArrayList<Client> listeCli = new ArrayList<>();
		try {
			// Recherche des clients en BD. cf. AccessClient > getClients(.)
			// numCompte != -1 => recherche sur numCompte
			// numCompte == -1 et debutNom non vide => recherche nom/prenom
			// numCompte == -1 et debutNom vide => recherche tous les clients

			Access_BD_Client ac = new Access_BD_Client();
			listeCli = ac.getClients(this.dailyBankState.getEmployeActuel().idAg, _numCompte, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCli = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCli = new ArrayList<>();
		}
		return listeCli;
	}
	
	public void simulerEmprunt() {
        EmpruntManagement em = new EmpruntManagement(this.primaryStage, this.dailyBankState);
        em.doEmpruntManagementDialog();
        
	}
}
