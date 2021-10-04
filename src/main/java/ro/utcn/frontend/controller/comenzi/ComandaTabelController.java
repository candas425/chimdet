package ro.utcn.frontend.controller.comenzi;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Manager;
import ro.utcn.backend.backendservices.CommandService;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Command;
import ro.utcn.backend.model.enums.TipFisier;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.InitializeService;
import ro.utcn.frontend.frontendServices.PopUpService;
import ro.utcn.frontend.frontendServices.TableService;
import ro.utcn.frontend.importer.ExcelParser;
import ro.utcn.backend.backendservices.BusinessService;
import ro.utcn.frontend.importer.PDFParser;
import ro.utcn.helper.GeneralHelper;
import ro.utcn.helper.JavaFxComponentsHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;


/**
 * Created by Lucian on 4/9/2017.
 */

@Component
public class ComandaTabelController extends GeneralActions implements Initializable {

    private Logger LOGGER = LogManager.getLogger(ComandaTabelController.class);

    @FXML
    public AnchorPane comandaTabelAnchoPane;

    private TableView comandaTabel;

    @Autowired
    private InitializeService initializeService;
    @Autowired
    private ExcelParser excelParser;
    @Autowired
    private PDFParser pdfParser;
    @Autowired
    private Manager manager;
    @Autowired
    private TableService tableService;
    @Autowired
    private JavaFxComponentsHelper javaFxComponentsHelper;
    @Autowired
    private PopUpService popUpService;
    @Autowired
    private CommandService commandService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comandaTabel = new TableView();
        try {
            initializeService.loadInitialComenziTablePage(comandaTabel, comandaTabelAnchoPane, manager.getBusinessFromComandaTabel());
        } catch (Exception e) {
            alertWithError(e.getMessage());
        }
        comandaTabelAnchoPane.setOnDragDropped(this::mouseDragDropped);
        comandaTabelAnchoPane.setOnDragOver(GeneralHelper::mouseDragEnter);
    }

    private void mouseDragDropped(final DragEvent e) {
        Dragboard db = e.getDragboard();
        if (db.hasFiles()) {
            if (db.getFiles().size() > 1 && (db.getFiles().get(1) != null && db.getFiles().get(1).length() > 3)) {
                alertWithError("Doar cate un fisier");
                return;
            }

            // Only get the first file from the list
            File file = db.getFiles().get(0);
            String extension = GeneralHelper.getExtension(file.getName());
            try {
                if (extension.toUpperCase().equals(TipFisier.PDF.toString())) {
                    pdfParser.parseFileAndSave(file, manager.getBusinessFromComandaTabel(), comandaTabel, null);
                } else {
                    excelParser.parseFileAndSave(file, manager.getBusinessFromComandaTabel(), comandaTabel, null);
                }
                alertWithDetails("Command a fost preluata");
            } catch (IOException e1) {
                alertWithError("Fisierul nu a putut fi parsat");
            } catch (GeneralExceptions generalExceptions) {
                alertWithError(generalExceptions.getMessage());
            } catch (Exception e2) {
                alertWithError("Fisier gresit");
            }

        }
        e.consume();
    }

    //stergere totala
    public void stergereTotala() {
        Business business = manager.getBusinessFromComandaTabel();
        List<Command> commandList = commandService.getCommandsAvailableInTableComanda(business);
        commandList.forEach(command -> {
            command.setAvailableInTableComanda(false);
            try {
                commandService.updateCommand(command);
            } catch (PersistanceException e) {
                LOGGER.error(e);
                alertWithError(e.getMessage());
            }
        });

        try {
            tableService.refreshComandaTabelWithData(comandaTabel, manager.getBusinessFromComandaTabel());
            alertWithDetails("Cantitatile au fost sterse");
        } catch (Exception e) {
            LOGGER.error(e);
            alertWithError(e.getMessage());
        }
    }

    //printare
    public void printare() {
        try {
            javaFxComponentsHelper.printEverything(comandaTabel);
        } catch (GeneralExceptions generalExceptions) {
            alertWithDetails(generalExceptions.getMessage());
        }
    }

    //cantitate oras
    public void cantitateOras() {
        try {
            popUpService.showPopUpOneChoiceForCantitateOras(comandaTabel);

        } catch (Exception e) {
            LOGGER.debug(e);
            alertWithError(e.getMessage());
        }
    }

    //resetare oras
    public void resetareOras() {
        try {
            popUpService.showPopUpRemovingCantitateOras(manager.getBusinessFromComandaTabel(), comandaTabel);
        } catch (Exception e) {
            LOGGER.debug(e);
            alertWithError(e.getMessage());
        }
    }
}
