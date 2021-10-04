package ro.utcn.frontend.controller.firma;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Manager;
import ro.utcn.backend.backendservices.BusinessService;
import ro.utcn.backend.model.Business;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.frontend.frontendServices.InitializeService;
import ro.utcn.frontend.frontendServices.PopUpService;
import ro.utcn.frontend.frontendServices.TableService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * Created by Lucian on 4/9/2017.
 */

@Component
public class FirmaController extends GeneralActions implements Initializable {

    private Logger LOGGER = LogManager.getLogger(FirmaController.class);

    @FXML
    public TextField filterField;
    @FXML
    public AnchorPane firmeAnchoPane;

    @Autowired
    private InitializeService initializeService;
    @Autowired
    private PopUpService popUpService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private TableService tableService;
    @Autowired
    private Manager manager;

    private TableView<Business> tableFirme;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableFirme = new TableView<>();
        initializeService.loadInitialFirmePage(tableFirme, firmeAnchoPane, filterField);
    }

    public void adaugaFirma() {
        popUpService.showPopUpMultipleChoicesForFirma(tableFirme, filterField);
    }

    public void stergeFirma() {
        Business business = tableFirme.getSelectionModel().getSelectedItem();
        if (business != null) {
            try {
                businessService.deleteBusiness(business);
                tableService.refreshItemsFromFirmaTableWithFilter(filterField, tableFirme);
                alertWithDetails("Business a fost stearsa");
            } catch (Exception e) {
                LOGGER.info("Failed to delete business", e);
                alertWithError("Business nu a putut fi stearsa deoarece exista in comenzi deja");
            }
        } else {
            alertWithError("Selectati o business din lista");
        }
    }

    public void listaPreturi() {
        Business business = tableFirme.getSelectionModel().getSelectedItem();
        if (business != null) {
            try {
                manager.setBusinessFromComandaTabel(business);
                manager.loadPreturiPage();
            } catch (IOException e) {
                alertWithError(e.getMessage());
            }
        } else {
            alertWithError("Selectati o business din lista");
        }
    }
}
