package ro.utcn.frontend.controller.angajati;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Manager;
import ro.utcn.backend.backendservices.HolidayService;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Holiday;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.frontend.frontendServices.InitializeService;
import ro.utcn.frontend.frontendServices.PopUpService;
import ro.utcn.frontend.frontendServices.TableService;

import java.net.URL;
import java.util.ResourceBundle;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * Concediu
 * Created by Lucian on 6/5/2017.
 */

@Component
public class ConcediuController extends GeneralActions implements Initializable {

    private Logger LOGGER = LogManager.getLogger(ConcediuController.class);

    @FXML
    public AnchorPane concediuAnchorPane;
    @FXML
    public TextField filterField;

    TableView<Holiday> holidayCocendiuTableView;

    @Autowired
    private InitializeService initializeService;
    @Autowired
    private Manager manager;
    @Autowired
    private PopUpService popUpService;
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private TableService tableService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        holidayCocendiuTableView = new TableView<>();
        initializeService.loadInitialConcediuPage(holidayCocendiuTableView, concediuAnchorPane, filterField, manager.getEmployee());
    }

    public void adaugaConcediu() {
        popUpService.showPopUpMultipleChoicesForZiLibera(holidayCocendiuTableView, filterField, manager.getEmployee());
    }

    public void stergeConcediu() {
        Holiday holiday = holidayCocendiuTableView.getSelectionModel().getSelectedItem();
        if (holiday != null) {
            try {
                holidayService.deleteHoliday(holiday);
                tableService.refreshItemsFromHolidayTable(filterField, holidayCocendiuTableView, manager.getEmployee());
                alertWithDetails("Ziua de concediu a fost stearsa");
            } catch (PersistanceException e) {
                LOGGER.error(e);
                alertWithError("Ziua de concediu nu a putut fi stearsa");
            }
        } else {
            alertWithError("Alege o zi de concediu din lista");
        }
    }
}
