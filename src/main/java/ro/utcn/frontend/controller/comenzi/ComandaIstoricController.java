package ro.utcn.frontend.controller.comenzi;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Manager;
import ro.utcn.backend.backendservices.CommandService;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Command;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.frontend.frontendServices.InitializeService;
import ro.utcn.frontend.frontendServices.PopUpService;
import ro.utcn.frontend.frontendServices.TableService;
import ro.utcn.frontend.frontendServices.helper.MailMessage;
import ro.utcn.frontend.importer.WordDeclaratieParser;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * Istoric
 * <p>
 * Created by Lucian on 5/8/2017.
 */
@Component
public class ComandaIstoricController extends GeneralActions implements Initializable {

    @FXML
    public AnchorPane comandaIstoricAnchorPane;
    @FXML
    public TextField filterField;

    @Autowired
    private Manager manager;
    @Autowired
    private InitializeService initializeService;
    @Autowired
    private WordDeclaratieParser wordDeclaratieParser;
    @Autowired
    private CommandService commandService;
    @Autowired
    private TableService tableService;
    @Autowired
    private PopUpService popUpService;

    public TableView<Command> tableViewComandaIstoric;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableViewComandaIstoric = new TableView<>();
        initializeService.loadInitialComandaIstoricPage(tableViewComandaIstoric, comandaIstoricAnchorPane, manager.getBusinessFromComandaTabel(), filterField);
    }

    public void printPagina1() {
        List<Command> comandList = tableViewComandaIstoric.getSelectionModel().getSelectedItems();
        if (comandList.size() > 0) {
            try {
                wordDeclaratieParser.parseWordFileFirstPage(comandList.get(0));
                alertWithDetails("Fisierul a fost salvat in folderul de 'files' si are numele de declaratie1Result.docx, si acum se printeaza...");
            } catch (Exception e) {
                alertWithError(e.getMessage());
            }
        } else {
            alertWithError("Selectati cel putin o comanda din lista");
        }
    }

    public void printPagine2() {
        List<Command> comandList = tableViewComandaIstoric.getSelectionModel().getSelectedItems();
        if (comandList.size() > 0) {
            try {
                String message = wordDeclaratieParser.parseWordFileSecondPage(comandList);
                if (message.length() > 1) {
                    alertWithError(message);
                }
                alertWithDetails("Fisierul a fost salvat in folderul de 'files' si are numele de declaratie2Result.docx, si acum se printeaza...");
            } catch (Exception e) {
                alertWithError(e.getMessage());
            }
        } else {
            alertWithError("Selectati cel putin o comanda din lista");
        }
    }

    public void stergeComanda() {
        Command command = tableViewComandaIstoric.getSelectionModel().getSelectedItem();
        if (command != null) {
            try {
                commandService.deleteCommand(command);
                tableService.refreshItemsFromComandaIstoricTable(filterField, tableViewComandaIstoric, manager.getBusinessFromComandaTabel());
                alertWithDetails("Command din data " + command.getData() + " din orasul " + command.getOras() + (command.getNumeFisier() != null ? " cu numele fisierului " + command.getNumeFisier() : "") + " a fost stearsa");
            } catch (PersistanceException e) {
                alertWithError(e.getMessage());
            }
        } else {
            alertWithError("Selectati cel putin o command din lista");
        }
    }

    public void trimiteMail() {
        List<Command> comandList = tableViewComandaIstoric.getSelectionModel().getSelectedItems();
        if (comandList.size() > 0) {
            MailMessage mailMessage = new MailMessage();
            mailMessage.setSubject("Confirmare comanda");
            mailMessage.setMailTo(manager.getBusinessFromComandaTabel().getMail());

            if (comandList.size() == 1) {
                if (comandList.get(0).getNumeFisier() != null) {
                    mailMessage.setMessage("Command aferenta fisierului cu numele " + comandList.get(0).getNumeFisier() + " a fost confirmata");
                } else {
                    mailMessage.setMessage("Command din data " + comandList.get(0).getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) + " a fost confirmata");
                }
            } else {

                StringBuilder comandaDatas = new StringBuilder();
                StringBuilder message = new StringBuilder();

                boolean withFile = false;
                for (Command command : comandList) {
                    if (command.getNumeFisier() != null) {
                        withFile = true;
                        comandaDatas.append(command.getNumeFisier()).append("\n");
                    } else {
                        comandaDatas.append(command.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))).append("\n");
                    }
                }

                if (withFile) {
                    message.append("Comenzile aferente fisierelor cu numele\n");
                    message.append(comandaDatas);
                } else {
                    message.append("Comenzile din datele\n");
                    message.append(comandaDatas);
                }


                message.append("au fost confirmate.");
                mailMessage.setMessage(message.toString());
            }

            try {
                popUpService.showPopUpForTrimitereMail(mailMessage, comandList, filterField, tableViewComandaIstoric, manager.getBusinessFromComandaTabel());
            } catch (GeneralExceptions generalExceptions) {
                alertWithError(generalExceptions.getMessage());
            }
        } else {
            alertWithError("Selectati cel putin o comanda din lista");
        }
    }
}
