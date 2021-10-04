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
 * Holiday
 * <p>
 * Created by Lucian on 6/5/2017.
 */

@Component
public class HolidayController extends GeneralActions implements Initializable {

    private Logger LOGGER = LogManager.getLogger(HolidayController.class);

    @FXML
    public AnchorPane holidayAnchorPane;
    @FXML
    public TextField filterField;

    TableView<Holiday> holidayTableView;

    @Autowired
    private InitializeService initializeService;
    @Autowired
    private PopUpService popUpService;
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private TableService tableService;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        holidayTableView = new TableView<>();
        initializeService.loadInitialHolidayPage(holidayTableView, holidayAnchorPane, filterField);
    }

    public void adaugaZiLibera() {
        popUpService.showPopUpMultipleChoicesForZiLibera(holidayTableView, filterField, null);
    }

    public void stergeZiLibera() {
        Holiday holiday = holidayTableView.getSelectionModel().getSelectedItem();
        if (holiday != null) {
            try {
                holidayService.deleteHoliday(holiday);
                tableService.refreshItemsFromHolidayTable(filterField, holidayTableView, null);
                alertWithDetails("Ziua libera a fost stersa");
            } catch (PersistanceException e) {
                LOGGER.error(e);
                alertWithError("Ziua libera nu a putut fi stersa");
            }
        } else {
            alertWithError("Alege o zi libera din lista");
        }
    }
}
