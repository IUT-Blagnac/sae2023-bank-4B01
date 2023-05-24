package application.view;

import java.awt.Desktop;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.OperationsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

/**
 * 
 * Cette classe est le contrôleur de la fenêtre de gestion des comptes d'un client.
 *
 */
public class ComptesManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ComptesManagementController
	private ComptesManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> oListCompteCourant;

	/**
	 * 
	 * Initialise le contexte de la fenêtre
	 * 
	 * @param _containingStage
	 * @param _cm
	 * @param _dbstate
	 * @param client
	 * 
	 */
	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	/**
	 * 
	 * Configure la fenêtre et initialise les données.
	 * 
	 */
	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}

	/**
	 * 
	 * Affiche la fenêtre de dialogue.
	 * 
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 * 
	 * Ferme la fenêtre de gestion des comptes.
	 * 
	 * @param e
	 * @return null
	 */
	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;
	@FXML
	private Button btnPDF;
	@FXML
	private Button btnPrelevements;

	/**
	 * 
	 * Ferme la fenêtre de gestion des comptes.
	 * 
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * 
	 * Ouvre la fenêtre de gestion des opérations pour le compte sélectionné
	 * 
	 */
	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			this.cmDialogController.gererOperationsDUnCompte(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}

	/**
	 * 
	 * Modifie le compte sélectionné
	 * 
	 */
	@FXML
	private void doModifierCompte() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		
		if (selectedIndice >= 0) {
			CompteCourant cptMod = this.oListCompteCourant.get(selectedIndice); //compte à modifier
			CompteCourant result = this.cmDialogController.modifierCompte(cptMod);
			if (result != null) {
				this.oListCompteCourant.set(selectedIndice, result);
			}
			
		}
		
		this.loadList();
		this.validateComponentState();
	}
	

	
	/**
	 * 
	 * Cree un nouveau compte
	 * 
	 */
	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cmDialogController.creerNouveauCompte();
		if (compte != null) {
			this.oListCompteCourant.add(compte);
		}
	}
	
	
	/**
     * 
     * Supprime le compte sélectionné.
     * 
     */
    @FXML
    private void doSupprimerCompte() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
            if(cpt.solde == 0) {
                this.cmDialogController.cloturerCompte(cpt);
            }
            else {
            	Alert alerteCptNonVide = new Alert(AlertType.WARNING);
            	alerteCptNonVide.setHeaderText("Le solde du compte n'est pas vide");
            	alerteCptNonVide.show();
            }
        }
        this.loadList();
        this.validateComponentState();
    }
    
    /**
     * 
     * Permet de générer un relevé mensuel en PDF des informations des opérations sur les comptes d'un client
     * 
     */
	@SuppressWarnings("deprecation")
	@FXML
    private void doGenererPDF() {
    	Document doc = new Document();
		
		try {
			
			PdfWriter.getInstance(doc, new FileOutputStream("Releve_Mensuel_" + this.clientDesComptes.prenom + "_" + this.clientDesComptes.nom + "_" + LocalDate.now().getMonthValue() +"_" + LocalDate.now().getYear() +".pdf"));
			
			doc.open();

			
			Paragraph par1 = new Paragraph("	Relevé mensuel du client", FontFactory.getFont(FontFactory.COURIER_BOLD, 24));
			
			Paragraph par2 = new Paragraph("	" + this.clientDesComptes.prenom + " " + this.clientDesComptes.nom, FontFactory.getFont(FontFactory.COURIER_BOLD, 24));
			
			Paragraph par3 = new Paragraph("	" + LocalDate.now().getMonthValue() +" / " + LocalDate.now().getYear(), FontFactory.getFont(FontFactory.COURIER_BOLD, 24));
			
			PdfPCell cell1 = new PdfPCell(par1);
			cell1.setBorder(Rectangle.BOX);
			cell1.setBorderWidth(1f);
			
			PdfPCell cell2 = new PdfPCell(par2);
			cell2.setBorder(Rectangle.BOX);
			cell2.setBorderWidth(1f);
			
			PdfPCell cell3 = new PdfPCell(par3);
			cell3.setBorder(Rectangle.BOX);
			cell3.setBorderWidth(1f);

			PdfPTable table1 = new PdfPTable(1); 
			table1.setWidthPercentage(100);
			table1.addCell(cell1);
			table1.addCell(cell2);
			table1.addCell(cell3);
			
			doc.add(table1);
			
			
			doc.add(new Paragraph(" "));
			doc.add(new Paragraph(" "));
			doc.add(new Paragraph("------------------------------------------------------------"));
			doc.add(new Paragraph(" "));
			
			for(int i = 0 ; i < this.oListCompteCourant.size() ; i++) {
				doc.add(new Chunk(i+1 + "e compte : ", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 14)));
				doc.add(new Paragraph("Numéro de compte : "+this.oListCompteCourant.get(i).toString()));
				doc.add(new Paragraph(" "));
				
				OperationsManagement ops = new OperationsManagement(primaryStage, dailyBankState, clientDesComptes, this.oListCompteCourant.get(i));
				
				ArrayList<Operation> listOp = ops.operationsEtSoldeDunCompte().getRight();

				for(int j = 0 ; j < listOp.size() ; j ++) {
					if(listOp.get(j).dateOp.getMonth() == LocalDate.now().getMonthValue());{
						doc.add(new Paragraph(listOp.get(j).toString()));	
					}
				}
				doc.add(new Paragraph(" "));
				doc.add(new Paragraph("------------------------------------------------------------"));
				doc.add(new Paragraph(" "));
			}
			
			
			doc.close();
			Desktop.getDesktop().open(new File("releve_mensuel_" + this.clientDesComptes.prenom + "_" + this.clientDesComptes.nom + "_" + LocalDate.now().getMonthValue() +"_" + LocalDate.now().getYear() +".pdf"));

		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		} catch (DocumentException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}

    }
    
	@FXML
	private void doVoirPrelevements() {
		this.cmDialogController.gererPrelevements(this.clientDesComptes);
	}

    
	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cmDialogController.getComptesDunClient();
		this.oListCompteCourant.clear();
		this.oListCompteCourant.addAll(listeCpt);
	}

	private void validateComponentState() {
		this.btnModifierCompte.setDisable(false);
		this.btnSupprCompte.setDisable(false);
		this.btnPrelevements.setDisable(false);

		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnVoirOpes.setDisable(false);
			this.btnModifierCompte.setDisable(false);
			this.btnSupprCompte.setDisable(false);
		} else {
			this.btnVoirOpes.setDisable(true);
			this.btnModifierCompte.setDisable(true);
			this.btnSupprCompte.setDisable(true);
		}
		
		
	}
	
	
}
