package ro.utcn.frontend.controller.angajati;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Constants;
import ro.utcn.Manager;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.frontend.frontendServices.InitializeService;
import ro.utcn.frontend.importer.ExcelPontajParser;
import ro.utcn.helper.JavaFxComponentsHelper;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * Pontaj
 * Created by Lucian on 6/3/2017.
 */

@Component
public class PontajController extends GeneralActions implements Initializable {

    private Logger LOGGER = LogManager.getLogger(PontajController.class);

    @FXML
    public AnchorPane pontajAnchorPane;
    public TableView tablePontajView;
    @FXML
    public Label labelLuna;

    @Autowired
    private InitializeService initializeService;
    @Autowired
    private ExcelPontajParser excelPontajParser;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            tablePontajView = new TableView();
            labelLuna = new Label();
            initializeService.loadInitialPontajPage(tablePontajView, pontajAnchorPane, labelLuna);
        } catch (GeneralExceptions generalExceptions) {
            alertWithError(generalExceptions.getMessage());
        }
    }

    public void generarePontaj() {
        try {

            String[] text = ((Label) pontajAnchorPane.getChildren().get(3)).getText().split(" -");
            String luna = text[0];
            String anul = text[1];

            excelPontajParser.parseFile((List<List<String>>) tablePontajView.getItems(), luna, anul);
            JavaFxComponentsHelper.printFile(Constants.FILES_LOCATION + Constants.PONTAJ_RESULT_FILE);

            alertWithDetails("Fisierul a fost salvat in folderul de 'files' si are numele de pontajResult, si acum se printeaza...");
        } catch (IOException | GeneralExceptions e) {
            LOGGER.error(e);
            alertWithError(e.getMessage());
        }
    }
}
