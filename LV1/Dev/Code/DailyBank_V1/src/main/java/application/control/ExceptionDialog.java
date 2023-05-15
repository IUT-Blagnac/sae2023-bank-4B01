package application.control;

import application.DailyBankApp;

import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ExceptionDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.orm.exception.ApplicationException;

/**
 * 
 * Cette classe permet d'afficher une boîte de dialogue en cas d'erreur lors de l'exécution de l'application.
 * Elle est appelée depuis une exception attrapée dans l'application.
 *
 */
public class ExceptionDialog {

	private Stage primaryStage;
	private ExceptionDialogController edcViewController;

	/**
	 * 
	 * Constructeur de la classe ExceptionDialog
	 * 
	 * @param _parentStage la fenêtre parente de la boîte de dialogue
	 * @param _dbstate l'état actuel de l'application
	 * @param ae l'exception à afficher dans la boîte de dialogue
	 * 
	 */
	public ExceptionDialog(Stage _parentStage, DailyBankState _dbstate, ApplicationException ae) {

		try {
			FXMLLoader loader = new FXMLLoader(ExceptionDialogController.class.getResource("exceptiondialog.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 700 + 20, 400 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Opération impossible");
			this.primaryStage.setResizable(false);

			this.edcViewController = loader.getController();
			this.edcViewController.initContext(this.primaryStage, _dbstate, ae);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Affiche la boîte de dialogue 
	 * 
	 */
	public void doExceptionDialog() {
		this.edcViewController.displayDialog();
	}

	/*
	 * Test : ApplicationException ae = new ApplicationException(Table.NONE,
	 * Order.OTHER, "M", null ); ExceptionDialogTemp ed = new
	 * ExceptionDialogTemp(primaryStage, dbs, ae); ed.doExceptionDisplay();
	 */
}
