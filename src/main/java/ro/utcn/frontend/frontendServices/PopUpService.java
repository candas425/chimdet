package ro.utcn.frontend.frontendServices;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.utcn.backend.backendservices.*;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.*;
import ro.utcn.backend.model.enums.TipConcediu;
import ro.utcn.backend.model.enums.TipFirma;
import ro.utcn.backend.model.enums.TipProdus;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.helper.ListRowComanda;
import ro.utcn.frontend.frontendServices.helper.MailMessage;
import ro.utcn.frontend.frontendServices.helper.TableRowComanda;
import ro.utcn.helper.GeneralHelper;
import ro.utcn.helper.JavaFxComponentsHelper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static ro.utcn.Constants.CONTINUT_EMAIL;
import static ro.utcn.exceptions.GeneralExceptions.CONTINUT_EMAIL_EXCEPTION;
import static ro.utcn.exceptions.GeneralExceptions.MAIL_SUCCESS;
import static ro.utcn.helper.GeneralHelper.getCantity;

/**
 * Pop-up code
 * <p>
 * Created by Lucian on 4/7/2017.
 */

@Service
public class PopUpService {

    private Logger LOGGER = LogManager.getLogger(PopUpService.class);

    private static final String ADD_BUTTON = "Adauga";

    @Autowired
    private BusinessService businessService;
    @Autowired
    private ProductService productService;
    @Autowired
    private TableService tableService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private GeneralHelper generalHelper;
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private CommandService commandService;

    public void showPopUpRemovingCantitateOras(Business business, TableView tableView) throws Exception {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Alege orasul");
        dialog.setHeaderText(null);

        // Set the button types.
        ButtonType addButtonType = new ButtonType("Alege", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 60, 10, 10));

        Map<String, String> oraseCuCheie = generalHelper.getOraseFromSetari();

        ChoiceBox<String> tipProdusChoiceBox = new ChoiceBox<>();
        tipProdusChoiceBox.setItems(FXCollections.observableArrayList(oraseCuCheie.keySet()));

        grid.add(new Label("Alege orasul"), 0, 0);
        grid.add(tipProdusChoiceBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional result = dialog.showAndWait();

        result.ifPresent(event -> {
            if (event == addButtonType) {
                if (tipProdusChoiceBox.getValue() != null) {
                    String oras = tipProdusChoiceBox.getValue();

                    List<Command> commandList = commandService.getCommandsForBusinessAndCity(business, oras);
                    if (commandList.size() > 0) {
                        for (Command command : commandList) {
                            command.setAvailableInTableComanda(false);
                            try {
                                commandService.updateCommand(command);
                            } catch (PersistanceException e) {
                                LOGGER.error(e);
                                alertWithError(e.getMessage());
                            }
                        }
                        alertWithDetails("Command/comenzile din orasul:" + oras + "nu mai sunt in tabel");

                        try {
                            tableService.refreshComandaTabelWithData(tableView, business);
                        } catch (Exception e) {
                            LOGGER.error(e);
                            alertWithError(e.getMessage());
                        }
                    }

                } else {
                    alertWithError("Alege un oras");
                }
            }
        });
    }


    public void showPopUpOneChoiceForCantitateOras(TableView<TableRowComanda> comandaTabel) throws Exception {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Cantitate oras");
        dialog.setHeaderText(null);

        // Set the button types.
        ButtonType addButtonType = new ButtonType("Cantitate", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 60, 10, 10));

        Map<String, String> oraseCuCheie = generalHelper.getOraseFromSetari();

        ChoiceBox<String> tipProdusChoiceBox = new ChoiceBox<>();
        tipProdusChoiceBox.setItems(FXCollections.observableArrayList(oraseCuCheie.keySet()));

        grid.add(new Label("Alege orasul"), 0, 0);
        grid.add(tipProdusChoiceBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional result = dialog.showAndWait();

        result.ifPresent(event -> {
            if (event == addButtonType) {
                if (tipProdusChoiceBox.getValue() != null) {

                    String oras = tipProdusChoiceBox.getValue();

                    List<Double> doubleList = null;
                    Map<Double, Double> flacoaneMap = new HashMap<>();
                    Map<Double, Double> sumMap = new HashMap<>();
                    try {
                        doubleList = generalHelper.getCantitatiProdusDisponibile();
                    } catch (Exception e) {
                        LOGGER.error(e);
                        alertWithDetails(e.getMessage());
                    }

                    double sumOfAll = 0.0;


                    for (TableRowComanda tableRowComanda : comandaTabel.getItems()) {
                        if (!StringHelper.isEmpty(tableRowComanda.getListMap().get(oras))) {
                            Double cantitate = tableRowComanda.getCantitateProdus();
                            Double bucati = Double.parseDouble(tableRowComanda.getListMap().get(oras));

                            sumOfAll += cantitate * bucati;
                            flacoaneMap.put(cantitate, flacoaneMap.get(cantitate) != null ? flacoaneMap.get(cantitate) + bucati : 0.0 + bucati);
                            sumMap.put(cantitate, sumMap.get(cantitate) != null ? sumMap.get(cantitate) + cantitate * bucati : 0.0 + cantitate * bucati);
                        }
                    }

                    String message = "";
                    for (Double d : doubleList) {
                        if (flacoaneMap.get(d) != null && sumMap.get(d) != null) {
                            message += "Sunt " + flacoaneMap.get(d) + " flacoane de " + d + "L" + " - Greutate: " + sumMap.get(d) + " kg\n";
                        }
                    }

                    message += "Greutate totala: " + sumOfAll + " kg";

                    alertWithDetails(message);
                } else {
                    alertWithError("Alege un oras");
                }
            }
        });
    }


    public void showPopUpMultipleChoicesForProdus(TextField textField, TableView tableView) throws Exception {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Adaugare Product");
        dialog.setHeaderText(null);

        // Set the button types.
        ButtonType addButtonType = new ButtonType(ADD_BUTTON, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 10));

        TextField nume = new TextField();
        nume.setPromptText("Nume");

        ChoiceBox<String> cantitateChoiceBox = new ChoiceBox<>();
        cantitateChoiceBox.setItems(FXCollections.observableArrayList(generalHelper.getCantitatiProdusDisponibileWithStrings()));

        ChoiceBox<TipProdus> tipProdusChoiceBox = new ChoiceBox<>();
        tipProdusChoiceBox.setItems(FXCollections.observableArrayList(TipProdus.values()));

        grid.add(new Label("Nume:"), 0, 0);
        grid.add(nume, 1, 0);
        grid.add(new Label("Cantitate(L):"), 0, 1);
        grid.add(cantitateChoiceBox, 1, 1);
        grid.add(new Label("Tip Product:"), 0, 2);
        grid.add(tipProdusChoiceBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Product product = new Product();
                product.setNume(nume.getText());
                product.setCantitate(cantitateChoiceBox.getValue() != null ? Double.parseDouble(cantitateChoiceBox.getValue()) : 0.0);
                product.setTip(tipProdusChoiceBox.getValue());
                return product;
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(produs -> {
            if (produs.getNume() == null || produs.getCantitate() == 0 || produs.getTip() == null) {
                alertWithError("Detaliile despre produs nu au fost introduse corect");
            } else {
                try {
                    productService.saveProduct(produs);
                    tableService.refreshItemsFromProdusTableWithFilter(textField, tableView);
                    alertWithDetails("A fost adaugat un produs nou");
                } catch (PersistanceException e) {
                    LOGGER.debug("Trying to save the produs created", e);
                    alertWithError("Numele produsului exista deja");
                }
            }
        });

    }

    public void showPopUpWithOneChoiceForProdusCantity(ListView<Product> listView, ListView<ListRowComanda> secondListView) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Alege Cantitatea");
        Product product = listView.getSelectionModel().getSelectedItem();
        dialog.setHeaderText(product.toString());

        // Set the button types.
        ButtonType addButtonType = new ButtonType(ADD_BUTTON, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 10));

        TextField cantitate = new TextField();
        cantitate.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        cantitate.setPromptText("Cantitate");

        grid.add(new Label("Cantitate:"), 0, 0);
        grid.add(cantitate, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return Double.parseDouble(cantitate.getText());
            }
            return null;
        });

        Optional<Double> result = dialog.showAndWait();

        if (result.isPresent()) {
            List<ListRowComanda> listRowComandas = new ArrayList<>();
            listRowComandas.addAll(secondListView.getItems());

            ListRowComanda listRowComanda = new ListRowComanda();
            listRowComanda.setProduct(product);
            listRowComanda.setCantitate(Double.parseDouble(cantitate.getText()));

            ListRowComanda rowToBeRemoved = null;
            for (ListRowComanda listRowComanda1 : listRowComandas) {
                if (listRowComanda1.getProduct().getId() == listRowComanda.getProduct().getId()) {
                    rowToBeRemoved = listRowComanda1;
                    listRowComanda.setCantitate(listRowComanda1.getCantitate() + listRowComanda.getCantitate());
                }
            }

            if (rowToBeRemoved != null) {
                listRowComandas.remove(rowToBeRemoved);
                listRowComandas.add(listRowComanda);
            } else {
                listRowComandas.add(listRowComanda);
            }
            secondListView.setItems(FXCollections.observableArrayList(listRowComandas));
        }

    }

    public void showPopUpMultipleChoicesForZiLibera(TableView tableView, TextField textField, Employee employee) {
        Dialog<Holiday> dialog = new Dialog<>();
        dialog.setTitle("Zi libera" + (employee != null ? "- " + employee.getNume() : ""));
        dialog.setHeaderText(null);

        // Set the button types.
        ButtonType addButtonType = new ButtonType(ADD_BUTTON, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 10));

        ChoiceBox<TipConcediu> tipConcediuChoiceBox = new ChoiceBox<>();
        tipConcediuChoiceBox.setItems(FXCollections.observableArrayList(TipConcediu.Co, TipConcediu.Bo, TipConcediu.Bp, TipConcediu.Am, TipConcediu.M, TipConcediu.I, TipConcediu.O, TipConcediu.N, TipConcediu.Prm, TipConcediu.Prb));
        tipConcediuChoiceBox.setValue(TipConcediu.Co);
        DatePicker datePickerFrom = new DatePicker();
        DatePicker datePickerTo = new DatePicker();

        grid.add(new Label("Din data :"), 0, 0);
        grid.add(datePickerFrom, 1, 0);
        grid.add(new Label("Pana la data :"), 0, 1);
        grid.add(datePickerTo, 1, 1);
        if (employee != null) {
            grid.add(new Label("Tip concediu:"), 0, 2);
            grid.add(tipConcediuChoiceBox, 1, 2);
        }
        dialog.getDialogPane().setContent(grid);

        Optional result = dialog.showAndWait();
        result.ifPresent(event -> {
            if (datePickerFrom.getValue() == null || datePickerTo.getValue() == null || datePickerFrom.getValue().isAfter(datePickerTo.getValue())) {
                alertWithError("Datele nu au fost introduse corect");
            } else {
                try {
                    LocalDate localDateFrom = datePickerFrom.getValue();
                    LocalDate localDateTo = datePickerTo.getValue();

                    if (localDateTo.isAfter(localDateFrom.plusDays(31))) {
                        throw new GeneralExceptions(GeneralExceptions.MAXIM_31_ZILE);
                    }

                    int count = 0;
                    List<Holiday> holidayList = new ArrayList<>();
                    while (localDateFrom.isBefore(localDateTo) || localDateFrom.equals(localDateTo)) {
                        if (localDateFrom.getDayOfWeek().equals(DayOfWeek.SUNDAY) || localDateFrom.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                            LOGGER.debug("Ziua libera nu poate sa fie Sambata sau Duminica");
                            localDateFrom = localDateFrom.plusDays(1);
                        } else {
                            Holiday holiday = new Holiday();
                            if (employee != null) {
                                holiday.setEmployee(employee);
                                holiday.setTipConcediu(tipConcediuChoiceBox.getValue());
                            } else {
                                holiday.setTipConcediu(TipConcediu.Libera);
                            }
                            holiday.setData(localDateFrom);
                            holidayList.add(holiday);

                            localDateFrom = localDateFrom.plusDays(1);
                            count++;
                        }
                    }

                    for (Holiday holiday : holidayList) {
                        holidayService.saveHoliday(holiday);
                    }

                    tableService.refreshItemsFromHolidayTable(textField, tableView, employee);

                    if (employee != null) {
                        LOGGER.debug("Scade " + count + " zile de concediu de la angajatul " + employee.getNume());
                        employee.setZileConcediuRamase(employee.getZileConcediuRamase() - count);
                        employeeService.update(employee);
                    }

                    alertWithDetails("Au fost adaugate un numar de " + count + " zile libere, weekendurile nu se pun ca zile libere");
                } catch (PersistanceException e) {
                    LOGGER.debug("Trying to save the holiday created", e);
                    alertWithError("O data setata exista in lista deja");
                } catch (GeneralExceptions generalExceptions) {
                    alertWithError(generalExceptions.getMessage());
                }
            }
        });
    }


    public void showPopUpMultipleChoicesForAngajat(TableView tableView, TextField textField) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Adaugare detalii angajat");
        dialog.setHeaderText(null);

        // Set the button types.
        ButtonType addButtonType = new ButtonType(ADD_BUTTON, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 10));

        TextField nume = new TextField();
        nume.setPromptText("Nume");
        TextField oreZi = new TextField();
        oreZi.setPromptText("Ore/Zi");
        oreZi.setTextFormatter(generalHelper.getNumberTextFieldFormatter());

        DatePicker datePicker = new DatePicker();

        grid.add(new Label("Nume:"), 0, 0);
        grid.add(nume, 1, 0);
        grid.add(new Label("Ore/Zi:"), 0, 1);
        grid.add(oreZi, 1, 1);
        grid.add(new Label("Data Angajarii:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Employee employee = new Employee();
                employee.setNume(nume.getText());
                employee.setOreLucruZi(Double.parseDouble(oreZi.getText()));
                employee.setDataAngajarii(datePicker.getValue());
                return employee;
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();

        result.ifPresent(angajat -> {
            if (angajat.getNume() == null || angajat.getOreLucruZi() == 0.0 || angajat.getDataAngajarii() == null) {
                alertWithError("Detalile despre angajat nu au fost introduse corect");
            } else {
                try {
                    employeeService.saveEmployee(angajat);
                    tableService.refreshItemsFromAngajatiTable(textField, tableView);
                    alertWithDetails("A fost adaugat un angajat nou");
                } catch (PersistanceException e) {
                    LOGGER.debug("Trying to save the angajat created", e);
                    alertWithError("Numele angajatului exista deja");
                }
            }
        });
    }

    public void showPopUpMultipleChoicesForFirma(TableView tableView, TextField textField) {
        Dialog<Business> dialog = new Dialog<>();
        dialog.setTitle("Adaugare Business");
        dialog.setHeaderText(null);

        // Set the button types.
        ButtonType addButtonType = new ButtonType(ADD_BUTTON, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 10));

        TextField nume = new TextField();
        nume.setPromptText("Nume");
        TextField cui = new TextField();
        cui.setPromptText("CUI/CNP");
        TextField judet = new TextField();
        judet.setPromptText("Judet");
        ChoiceBox<TipFirma> tipFirmaChoiceBox = new ChoiceBox<>();
        tipFirmaChoiceBox.setItems(FXCollections.observableArrayList(TipFirma.values()));

        grid.add(new Label("Nume:"), 0, 0);
        grid.add(nume, 1, 0);
        grid.add(new Label("CUI/CNP:"), 0, 1);
        grid.add(cui, 1, 1);
        grid.add(new Label("Judet:"), 0, 2);
        grid.add(judet, 1, 2);
        grid.add(new Label("Tip Business:"), 0, 3);
        grid.add(tipFirmaChoiceBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Business business = new Business();
                business.setNume(nume.getText());
                business.setCui(cui.getText());
                business.setTip(tipFirmaChoiceBox.getValue());
                business.setJudet(judet.getText());
                return business;
            }
            return null;
        });

        Optional<Business> result = dialog.showAndWait();

        result.ifPresent(firma -> {
            if (StringHelper.isEmpty(firma.getNume()) || StringHelper.isEmpty(firma.getCui()) || firma.getTip() == null || StringHelper.isEmpty(firma.getJudet())) {
                alertWithError("Detaliile despre firma nu au fost introduse corect");
            } else {
                try {
                    businessService.saveBusiness(firma);
                    tableService.refreshItemsFromFirmaTableWithFilter(textField, tableView);
                    alertWithDetails("A fost adaugata o firma noua");
                } catch (PersistanceException e) {
                    LOGGER.debug("Trying to save the firm created", e);
                    alertWithError("Numele firmei sau CUI-ul exista deja");
                }
            }
        });
    }

    public void shouProductsSold(DatePicker datePickerFrom, DatePicker datePickerTo, ChoiceBox<String> choiceBoxFirma) {
        if (datePickerFrom.getValue() != null && datePickerTo.getValue() != null && datePickerFrom.getValue().isBefore(datePickerTo.getValue())) {
            LocalDate localDateFrom = datePickerFrom.getValue();
            LocalDate localDateTo = datePickerTo.getValue();

            boolean notBusiness = choiceBoxFirma.getValue() == null || choiceBoxFirma.getValue().equals("Toate");

            Map<Double, Integer> productsSold;
            if (notBusiness) {
                productsSold = commandService.countProductFromCommandsFromDateToDate(localDateFrom.atStartOfDay(), localDateTo.atStartOfDay());
            } else {
                productsSold = commandService.countProductFromCommandsFromDateToDateWithBusinessName(localDateFrom.atStartOfDay(), localDateTo.atStartOfDay(), choiceBoxFirma.getValue());
            }

            long allProductsSold = getCantity(productsSold);
            StringBuilder message = new StringBuilder("Au fost vandute " + allProductsSold + " produse intre " + localDateFrom + " si " + localDateTo + "\n");


            for (Map.Entry<Double, Integer> entry : productsSold.entrySet()) {
                message.append(entry.getValue()).append(" de produse la cantitatea de ").append(entry.getKey()).append("\n");
            }

            alertWithDetails(message.toString());
        } else {
            alertWithError("Introdu ambele dati si prima data sa fie mai mica decat a 2-a");
        }
    }

    public void showPopUpForComandaIstoric(String message) {
        Dialog<Business> dialog = new Dialog<>();
        dialog.setTitle("Informare");
        dialog.setHeaderText(null);
        dialog.setContentText(message);

        // Set the button types.
        ButtonType addButtonType = new ButtonType("Print", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);


        Optional result = dialog.showAndWait();

        result.ifPresent(event -> {
            if (event == addButtonType) {
                try {
                    JavaFxComponentsHelper.printEverythingWithText(message);
                } catch (GeneralExceptions generalExceptions) {
                    alertWithError(generalExceptions.getMessage());
                }
            }
        });
    }

    public void showPopUpForTrimitereMail(MailMessage message, List<Command> commandList, TextField filterField, TableView<Command> tableViewComandaIstoric, Business businessFromComandaTabel) throws GeneralExceptions {
        Dialog<Business> dialog = new Dialog<>();
        dialog.setTitle("Informare");
        dialog.setHeaderText(null);

        // append semnatura la baza mesajului la email
        String semnatura = generalHelper.getSetareGeneralaFromSetari(CONTINUT_EMAIL, CONTINUT_EMAIL_EXCEPTION);
        message.setMessage(message.getMessage() + "\n\n" + semnatura);

        // Set the button types.
        ButtonType addButtonType = new ButtonType("Trimite", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 10));

        TextField mailTo = new TextField();
        mailTo.setText(message.getMailTo());
        TextField subiect = new TextField();
        subiect.setText(message.getSubject());
        TextArea mesaj = new TextArea();
        mesaj.setText(message.getMessage());

        grid.add(new Label("Mail-ul se trimite la adresa:"), 0, 0);
        grid.add(mailTo, 1, 0);
        grid.add(new Label("Subiect:"), 0, 1);
        grid.add(subiect, 1, 1);
        grid.add(new Label("Continut:"), 0, 2);
        grid.add(mesaj, 1, 2);


        dialog.getDialogPane().setContent(grid);

        Optional result = dialog.showAndWait();

        result.ifPresent(event -> {
            if (event == addButtonType) {
                if (!StringHelper.isEmpty(mailTo.getText()) && !StringHelper.isEmpty(subiect.getText()) && !StringHelper.isEmpty(mesaj.getText())) {
                    try {
                        MailMessage mailMessage = new MailMessage();
                        mailMessage.setMessage(mesaj.getText());
                        mailMessage.setSubject(subiect.getText());
                        mailMessage.setMailTo(mailTo.getText());

                        emailService.sendMail(mailMessage);

                        for (Command command : commandList) {
                            command.setConfirmare("Confirmat");
                            commandService.updateCommand(command);
                        }

                        tableService.refreshItemsFromComandaIstoricTable(filterField, tableViewComandaIstoric, businessFromComandaTabel);
                        alertWithDetails(MAIL_SUCCESS);
                    } catch (GeneralExceptions | PersistanceException generalExceptions) {
                        alertWithDetails(generalExceptions.getMessage());
                    }
                }
            }
        });
    }

    public static void alertWithDetails(String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informare");
        alert.setHeaderText(null);
        alert.setContentText(header);
        alert.showAndWait();
    }

    public static void alertWithError(String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText(null);
        alert.setContentText(header);
        alert.showAndWait();
    }
}
