package ro.utcn.frontend.controller.firma;

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
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Price;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.frontend.exporter.ExcelExporter;
import ro.utcn.frontend.frontendServices.InitializeService;
import ro.utcn.frontend.importer.ExcelPreturiParser;
import ro.utcn.helper.JavaFxComponentsHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;
import static ro.utcn.helper.JavaFxComponentsHelper.createLabel;

/**
 * Preturi
 * Created by Lucian on 5/31/2017.
 */

@Component
public class PreturiController extends GeneralActions implements Initializable {

    @FXML
    public AnchorPane preturiAnchoPane;
    @FXML
    public TextField filterField;

    private TableView<Price> tablePret;

    private Logger LOGGER = LogManager.getLogger(PreturiController.class);

    @Autowired
    private InitializeService initializeService;
    @Autowired
    private Manager manager;
    @Autowired
    private ExcelPreturiParser excelPreturiParser;
    @Autowired
    private ExcelExporter excelExporter;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Business business = manager.getBusinessFromComandaTabel();
        preturiAnchoPane.getChildren().add(createLabel(15, 30, 26, "Client:" + business));

        tablePret = new TableView<>();
        try {
            initializeService.loadInitialPreturiPage(tablePret, preturiAnchoPane, filterField, business);
        } catch (GeneralExceptions generalExceptions) {
            alertWithError(generalExceptions.getMessage());
        }
    }

    public void importaListaPreturi() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Alege un fisier");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel file", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(manager.getStage());
        if (selectedFile != null) {
            try {
                excelPreturiParser.parseFileAndSave(selectedFile, filterField, tablePret, manager.getBusinessFromComandaTabel());
                alertWithDetails("Preturile au fost salvate");
                LOGGER.info("Preturile au fost salvate");
            } catch (IOException | GeneralExceptions | PersistanceException e) {
                LOGGER.error("Preturile nu au fost salvate");
                alertWithError(e.getMessage());
            }
        } else {
            alertWithError("Alege un fisier");
        }
    }

    public void backFirme() {
        try {
            manager.loadFirmePage();
        } catch (IOException e) {
            alertWithError(e.getMessage());
        }
    }

    public void printeazaOferta() {
        List<Price> priceList = tablePret.getItems();
        int numberOfPrintings = priceList.size() / 40;
        if (((double) priceList.size() / 40) - numberOfPrintings > 0) {
            numberOfPrintings++;
        }


        int counter = 0;
        for (int i = 0; i < numberOfPrintings; i++) {
            StringBuilder message = new StringBuilder();

            message.append(manager.getBusinessFromComandaTabel()).append("\n\n");
            message.append("PRET UNITAR | CANTITATE | PRODUS").append("\n");

            while (priceList.size() != counter) {
                message.append(priceList.get(counter).getPretUnitar()).append(" lei").append(" - ").append(priceList.get(counter).getProduct().getCantitate()).append("L - ").append(priceList.get(counter).getProduct().getNume()).append("\n");
                if (counter == (39 + i * 40)) {
                    counter = 40 + i * 40;
                    break;
                }
                counter++;
            }

            try {
                JavaFxComponentsHelper.printEverythingWithText(message.toString());
            } catch (GeneralExceptions generalExceptions) {
                alertWithError(generalExceptions.getMessage());
            }
        }
    }

    public void exportaInFormatExcel() {
        Workbook workbook = excelExporter.createExcelFilePreturi(manager.getBusinessFromComandaTabel());
        JavaFxComponentsHelper.fileExporter(workbook, manager.getStage());
    }
}
