package application.control;
import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmpruntManagementController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


/**
 * 
 * Cette classe permet de gérer la fenêtre de gestion des simulation d'emprunt
 *
 */
public class EmpruntManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private EmpruntManagementController empruntViewController;

	
	/**
	 * Constructeur de la classe ClientsManagement
	 * 
	 * @param _parentStage Stage parent de la fenêtre
	 * @param _dbstate Etat courant de l'application
	 * 
	 */
	public EmpruntManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmpruntManagementController.class.getResource("EmpruntEcriture.fxml"));
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

			this.empruntViewController = loader.getController();
			this.empruntViewController.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**
    *
	  * Affiche la fenêtre de simulation d'emprunt
	  * 
	 */
	public void doClientManagementDialog() {
		this.empruntViewController.displayDialog();
	}
}
