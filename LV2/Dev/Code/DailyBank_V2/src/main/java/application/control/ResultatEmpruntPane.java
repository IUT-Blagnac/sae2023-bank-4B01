package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.EmpruntManagementController;
import application.view.ResultatEmpruntPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Emprunt;

public class ResultatEmpruntPane {
	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private ResultatEmpruntPaneController repViewController;

	
	/**
	 * Constructeur de la classe ResultatEmpruntPane
	 * 
	 * @param _parentStage Stage parent de la fenêtre
	 * @param _dbstate Etat courant de l'application
	 * 
	 */
	public ResultatEmpruntPane(Stage _parentStage, DailyBankState _dbstate) {
		// TODO Auto-generated constructor stub
		
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmpruntManagementController.class.getResource("resultatemprunt.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Simmulation d'emprunt");
			this.primaryStage.setResizable(false);

			this.repViewController = loader.getController();
			this.repViewController.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	 /**
    *
	  * Affiche la fenêtre de résultat d'emprunt7
	  * 
	 */
	public void doResultatEmpruntPaneDialog() {
		this.repViewController.displayDialog();
	}
	
	
}
