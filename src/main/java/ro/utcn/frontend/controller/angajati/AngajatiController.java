package ro.utcn.frontend.controller.angajati;

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
import ro.utcn.backend.backendservices.EmployeeService;
import ro.utcn.backend.model.Employee;
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
 * Angajati
 * Created by Lucian on 5/11/2017.
 */

@Component
public class AngajatiController extends GeneralActions implements Initializable {

    private Logger LOGGER = LogManager.getLogger(AngajatiController.class);

    @FXML
    public AnchorPane angajatiAnchorPane;
    @FXML
    public TextField filterField;

    @Autowired
    private InitializeService initializeService;
    @Autowired
    private PopUpService popUpService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private TableService tableService;
    @Autowired
    private Manager manager;

    public TableView<Employee> tableViewAngajat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableViewAngajat = new TableView<>();
        initializeService.loadInitialAngajatiPage(angajatiAnchorPane, tableViewAngajat, filterField);
    }

    public void adaugaAngajat() {
        popUpService.showPopUpMultipleChoicesForAngajat(tableViewAngajat, filterField);
    }

    public void stergeAngajat() {
        Employee employee = tableViewAngajat.getSelectionModel().getSelectedItem();
        if (employee != null) {
            try {
                employeeService.deleteEmployee(employee);
                tableService.refreshItemsFromAngajatiTable(filterField, tableViewAngajat);
                alertWithDetails("Angajatul a fost sters");
            } catch (Exception e) {
                LOGGER.info("Failed to delete employee", e);
                alertWithError("Angajatul nu a putut fi sters");
            }
        } else {
            alertWithError("Alege un employee din lista");
        }
    }

    public void concediuAngajat() {
        Employee employee = tableViewAngajat.getSelectionModel().getSelectedItem();
        if (employee != null) {
            try {
                manager.setEmployee(employee);
                manager.loadConcediuPage("Concediu - " + employee.getNume());
            } catch (IOException e) {
                LOGGER.error(e);
                alertWithError(e.getMessage());
            }
        } else {
            alertWithError("Alege un employee din lista");
        }
    }
}
