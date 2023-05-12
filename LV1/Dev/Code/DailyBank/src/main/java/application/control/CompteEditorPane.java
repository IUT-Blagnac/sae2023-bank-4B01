package application.control;

import application.DailyBankApp;

import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

/**
 * 
 * Classe représentant un panneau d'édition de compte courant. 
 * Cette classe est utilisée pour afficher une boîte de dialogue d'édition de compte courant.
 * Le panneau contient un formulaire permettant de modifier les informations du compte courant, 
 * comme le solde, le découvert autorisé, etc.
 *
 */
public class CompteEditorPane {

	private Stage primaryStage;
	private CompteEditorPaneController cepcViewController;

	
	public CompteEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(CompteEditorPaneController.class.getResource("compteeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un compte");
			this.primaryStage.setResizable(false);

			this.cepcViewController = loader.getController();
			this.cepcViewController.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Ouvre une boîte d'édition des comptes
	 * 
	 * @param client Le client à éditer
	 * @param cpte Le compte à éditer
	 * @param em Le mode d'édition à utiliser
	 * 
	 * @return Le compte créé
	 * 
	 */
	public CompteCourant doCompteEditorDialog(Client client, CompteCourant cpte, EditionMode em) {
		return this.cepcViewController.displayDialog(client, cpte, em);
	}
}
