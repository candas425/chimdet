package ro.utcn.frontend.controller.produse;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Manager;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.frontend.exporter.ExcelExporter;
import ro.utcn.frontend.frontendServices.PopUpService;
import ro.utcn.frontend.frontendServices.InitializeService;
import ro.utcn.frontend.frontendServices.TableService;
import ro.utcn.backend.model.Product;
import ro.utcn.backend.backendservices.ProductService;
import ro.utcn.frontend.importer.ExcelProdusParser;
import ro.utcn.helper.JavaFxComponentsHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * Created by Lucas on 4/15/2017.
 */

@Component
public class ProduseController extends GeneralActions implements Initializable {

    public AnchorPane produseAnchorPane;

    @FXML
    public TextField filterField;

    @Autowired
    private ExcelProdusParser excelProducParser;

    private Logger LOGGER = LogManager.getLogger(ProduseController.class);

    @Autowired
    private ProductService productService;
    @Autowired
    private InitializeService initializeService;
    @Autowired
    private TableService tableService;
    @Autowired
    private PopUpService popUpService;
    @Autowired
    private Manager manager;
    @Autowired
    private ExcelExporter excelExporter;

    public static TableView<Product> tableProduse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableProduse = new TableView<>();
        try {
            initializeService.loadInitialProdusePage(tableProduse, produseAnchorPane, filterField);
        } catch (Exception e) {
            alertWithError(e.getMessage());
        }
    }

    public void adaugaProdus() {
        try {
            popUpService.showPopUpMultipleChoicesForProdus(filterField, tableProduse);
        } catch (Exception e) {
            alertWithError(e.getMessage());
        }
    }

    public void stergeProdus() {
        Product product = tableProduse.getSelectionModel().getSelectedItem();
        if (product != null) {
            try {
                productService.deleteProduct(product);
                tableService.refreshItemsFromProdusTableWithFilter(filterField, tableProduse);
                alertWithDetails("Produsul a fost sters");
            } catch (Exception e) {
                LOGGER.error("Failed to delete product", e);
                alertWithError("Produsul nu a putut fi sters");
            }
        } else {
            alertWithError("Selectati un product din lista");
        }
    }

    public void importaProduse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Alege un fisier");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel file", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(manager.getStage());
        if (selectedFile != null) {
            try {
                excelProducParser.parseFileAndSave(selectedFile, filterField, tableProduse);
                alertWithDetails("Produsele au fost salvate");
            } catch (IOException | GeneralExceptions | PersistanceException e) {
                alertWithError(e.getMessage());
            }
        } else {
            alertWithError("Alege un fisier");
        }
    }

    public void exportaProduse() {
        Workbook workbook = excelExporter.createExcelFileProduse();
        JavaFxComponentsHelper.fileExporter(workbook, manager.getStage());
    }
}
