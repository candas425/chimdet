package ro.utcn.frontend.frontendServices;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.utcn.backend.backendservices.*;
import ro.utcn.backend.backendservices.BaseService;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.*;
import ro.utcn.backend.model.enums.TipConcediu;
import ro.utcn.backend.model.enums.TipFirma;
import ro.utcn.backend.model.enums.TipProdus;
import ro.utcn.backend.model.factory.IEntity;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.helper.DatePickerCellAngajat;
import ro.utcn.frontend.frontendServices.helper.DoubleStringTableConverter;
import ro.utcn.frontend.frontendServices.helper.TableRowComanda;
import ro.utcn.helper.GeneralHelper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ro.utcn.Constants.*;
import static ro.utcn.exceptions.GeneralExceptions.*;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;
import static ro.utcn.helper.GeneralHelper.formatter;

/**
 * Table
 * <p>
 * Created by Lucian on 4/13/2017.
 */

@Service
public class TableService {

    private Logger LOGGER = LogManager.getLogger(TableService.class);

    private ObservableList<String> trueFalseValues = FXCollections.observableArrayList(TRUE, FALSE);

    @Autowired
    private BusinessService businessService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CommandService commandService;
    @Autowired
    private GeneralHelper generalHelper;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private PopUpService popUpService;

    public void constructHolidayTable(TableView<Holiday> tableView, TextField filterField, Employee employee) {
        //pentru Produse Nume
        TableColumn<Holiday, String> descriptionCol = new TableColumn<>("Tip concediu");
        descriptionCol.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getTipConcediu() != null ? foo.getValue().getTipConcediu().getFullName() : ""));

        //column for date
        TableColumn<Holiday, LocalDate> dataZiLiberaCol = new TableColumn<>("Data");
        dataZiLiberaCol.setCellValueFactory(new PropertyValueFactory<>(Holiday.DATA));

        tableView.getColumns().addAll(descriptionCol, dataZiLiberaCol);

        refreshItemsFromHolidayTable(filterField, tableView, employee);
    }

    public void refreshItemsFromHolidayTable(TextField textField, TableView tableView, Employee employee) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Holiday> filteredData;
        if (employee == null) {
            filteredData = new FilteredList<>(FXCollections.observableArrayList(holidayService.getAllNonEmployeeHolidays()));
        } else {
            filteredData = new FilteredList<>(FXCollections.observableArrayList(holidayService.getAllForEmployee(employee)));
        }
        // 2. Set the filter Predicate whenever the filter changes.
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(holiday -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                return holiday.getData().toString().contains(newValue);
            });
        });
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Holiday> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator, Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }


    public void constructPontajTable(TableView<List<String>> tableView, LocalDate currentDate) {
        int numberOfDays = currentDate.getMonth().length(currentDate.isLeapYear());

        TableColumn<List<String>, String> numeCol = new TableColumn<>("Nume");
        numeCol.setCellValueFactory(event -> new ReadOnlyStringWrapper(event.getValue().get(0)));
        numeCol.setMinWidth(200);
        numeCol.setSortType(TableColumn.SortType.ASCENDING);
        tableView.getColumns().addAll(numeCol);

        for (int i = 1; i <= numberOfDays; i++) {
            LocalDate temmLocalDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), i);
            int finalI = i;
            if (temmLocalDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || temmLocalDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                TableColumn<List<String>, String> weekendCol = new TableColumn<>(String.valueOf(i));
                weekendCol.setCellValueFactory(event -> new ReadOnlyStringWrapper(event.getValue().get(finalI)));
                weekendCol.setSortable(false);
                tableView.getColumns().addAll(weekendCol);
            } else {
                TableColumn<List<String>, String> normalCol = new TableColumn<>(String.valueOf(i));
                normalCol.setCellValueFactory(event -> new ReadOnlyStringWrapper(event.getValue().get(finalI)));
                normalCol.setSortable(false);
                tableView.getColumns().addAll(normalCol);
            }
        }

        //populare date tabel
        List<Employee> employeeList = employeeService.getAll();
        List<List<String>> toShowInTable = new ArrayList<>();

        List<LocalDate> holidayList = new ArrayList<>();
        for (Holiday holiday : holidayService.getAllNonEmployeeHolidays()) {
            holidayList.add(holiday.getData());
        }


        for (int i = 0; i < employeeList.size(); i++) {
            List<String> coloaneTabel = new ArrayList<>();
            Employee employee = employeeList.get(i);
            coloaneTabel.add(employee.getNume());

            List<Holiday> angajatHolidayList = holidayService.getAllForEmployee(employee);

            int numberOfWorkedDays = 0;
            int numberOfConcediuOdihna = 0;
            for (int j = 1; j <= numberOfDays; j++) {
                LocalDate tempLocalDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), j);
                Holiday holiday = generalHelper.getHolidayWithDate(angajatHolidayList, tempLocalDate);
                if (tempLocalDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                        || tempLocalDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                           || holidayList.contains(tempLocalDate)
                             || employee.getDataAngajarii().isAfter(tempLocalDate)) {
                    coloaneTabel.add("X");
                } else if (holiday != null) {
                    coloaneTabel.add(holiday.getTipConcediu().getMask());
                    if (holiday.getTipConcediu().equals(TipConcediu.Co)) {
                        numberOfConcediuOdihna += employee.getOreLucruZi();
                    }
                } else {
                    coloaneTabel.add(String.valueOf((int) employee.getOreLucruZi()));
                    numberOfWorkedDays += employee.getOreLucruZi();
                }
            }

            coloaneTabel.add(String.valueOf(numberOfWorkedDays));
            coloaneTabel.add(String.valueOf(numberOfConcediuOdihna));

            toShowInTable.add(coloaneTabel);
        }

        tableView.setItems(FXCollections.observableArrayList(toShowInTable));
        tableView.getSortOrder().add(numeCol);
    }

    public void constructPreturiTable(TableView<Price> tableView, TextField filterField, Business business) throws GeneralExceptions {
        //coloana cu valoare
        TableColumn<Price, Double> valoareCol = (TableColumn<Price, Double>) createDoubleColumnPret(Price.VALOARE, "Price Unitar", priceService, true, filterField, tableView, business);

        String setareGeneralTVA = generalHelper.getSetareGeneralaFromSetari(VALOARE_TVA, VALOARE_TVA_EXCEPTION);

        //pentru valoare produs cu tva
        TableColumn<Price, String> valoareTvaCol = new TableColumn<>("Price cu TVA");
        valoareTvaCol.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getPretUnitar() != 0.0 ? String.valueOf(foo.getValue().getPretUnitar() + (foo.getValue().getPretUnitar() * Double.parseDouble(setareGeneralTVA)) / 100) : ""));

        //pentru Produse Nume
        TableColumn<Price, String> numeProdusCol = new TableColumn<>("Product");
        numeProdusCol.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getProduct() != null ? foo.getValue().getProduct().getNume() : ""));

        //pentru Produse Cantitate
        TableColumn<Price, String> cantitateCol = new TableColumn<>("Cantitate");
        cantitateCol.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getProduct() != null ? String.valueOf(foo.getValue().getProduct().getCantitate()) : ""));

        refreshItemsFromPreturiTable(filterField, tableView, business);

        tableView.getColumns().addAll(numeProdusCol, cantitateCol, valoareCol, valoareTvaCol);
    }

    public void refreshItemsFromPreturiTable(TextField textField, TableView tableView, Business business) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Price> filteredData = new FilteredList<>(FXCollections.observableArrayList(priceService.getAll(business)));

        // 2. Set the filter Predicate whenever the filter changes.
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(pret -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                return pret.getProduct().getNume().toLowerCase().toString().contains(newValue);
            });
        });
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Price> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator, Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }

    public void constructAngajatiTable(TableView<Employee> tableView, TextField filterField) {
        TableColumn<Employee, String> numeCol = (TableColumn<Employee, String>) createStringColumn(Employee.NUME, "Nume", employeeService, true);
        numeCol.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<Employee, String> cnpCol = (TableColumn<Employee, String>) createStringColumn(Employee.CNP, "CNP", employeeService, true);
        TableColumn<Employee, Double> oreLucruZiCol = (TableColumn<Employee, Double>) createDoubleColumn(Employee.ORE_LUCRU_ZI, "Ore de lucru pe zi", employeeService, true);
        TableColumn<Employee, Double> zileConcediuCol = (TableColumn<Employee, Double>) createDoubleColumn(Employee.ZILE_CONCEDIU_RAMASE, "Zile concediu ramase", employeeService, true);

        //column for date
        TableColumn<Employee, LocalDate> dataAngajariiCol = new TableColumn<>("Data Angajarii");
        dataAngajariiCol.setEditable(true);
        dataAngajariiCol.setCellValueFactory(new PropertyValueFactory<>(Employee.DATA_ANGAJARII));
        dataAngajariiCol.setCellFactory(col -> new DatePickerCellAngajat());
        dataAngajariiCol.setOnEditCommit(event -> updateWithProperty(event.getTableView().getItems().get(event.getTablePosition().getRow()), Employee.DATA_ANGAJARII, event.getNewValue(), employeeService));

        refreshItemsFromAngajatiTable(filterField, tableView);

        tableView.getColumns().addAll(numeCol, cnpCol, dataAngajariiCol, oreLucruZiCol, zileConcediuCol);
        tableView.getSortOrder().add(numeCol);
    }

    public void refreshItemsFromAngajatiTable(TextField textField, TableView tableView) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Employee> filteredData = new FilteredList<>(FXCollections.observableArrayList(employeeService.getAll()));

        // 2. Set the filter Predicate whenever the filter changes.
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(angajat -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                return angajat.getNume().toLowerCase().toString().contains(newValue);
            });
        });
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Employee> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator, Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }


    public void constructComandaIstoricTable(TableView<Command> tableView, Business business, TextField filterField) {
        //pentru LocalDateTime
        TableColumn<Command, String> dataColoana = new TableColumn<>("Data comenzii");
        dataColoana.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
                + (!StringHelper.isEmpty(foo.getValue().getNumeFisier()) ? " Nume fisier: " + foo.getValue().getNumeFisier() : "")));
        dataColoana.setMinWidth(300);

        //pentru Oras
        TableColumn<Command, String> orasColoana = new TableColumn<>("Oras");
        orasColoana.setCellValueFactory(new PropertyValueFactory<>(Command.ORAS));

        //pentru Business
        TableColumn<Command, String> firmaColoana = new TableColumn<>("Business");
        firmaColoana.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getBusiness() != null ? foo.getValue().getBusiness().getNume() : ""));

        //pentru confirmare coloana
        TableColumn<Command, String> confirmareColoana = new TableColumn<>("Mail trimis");
        confirmareColoana.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getConfirmare()));

        tableView.getColumns().addAll(dataColoana, orasColoana, firmaColoana, confirmareColoana);

        refreshItemsFromComandaIstoricTable(filterField, tableView, business);

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Command command = tableView.getSelectionModel().getSelectedItem();

                List<Product> productList = productService.getProductsByIds(command.getListaProduse().keySet());
                StringBuilder message = new StringBuilder();
                message.append(command.getBusiness().getNume()).append(" - ").append(command.getOras()).append(" - ").append(command.getData().format(formatter)).append("\n\n");

                for (Product product : productList) {
                    Integer cantitate = command.getListaProduse().get(product.getId());
                    message.append(product).append(" - ").append(cantitate).append("\n");
                }
                message.append("\n");
                message.append("Cantitate totala in KG: ").append(getTotalCantitty(command, productList));
                popUpService.showPopUpForComandaIstoric(message.toString());
            }
        });
    }

    public void refreshItemsFromComandaIstoricTable(TextField textField, TableView tableView, Business business) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Command> filteredData = new FilteredList<>(FXCollections.observableArrayList(commandService.getCommandsForBusiness(business)));

        // 2. Set the filter Predicate whenever the filter changes.
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(comanda -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                return comanda.getData().toString().contains(newValue);
            });
        });
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Command> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator, Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }


    public void constructComandaTableHeaderTable(TableView tableView, Business business) throws Exception {
        TableColumn<TableRowComanda, String> numarIdentificatorCol = (TableColumn<TableRowComanda, String>) createStringColumn(TableRowComanda.IDENTIFICATOR_PRODUS, "Nr.", null, false);
        String dimensiuneNumarIdentificatorProdus = generalHelper.getSetareGeneralaFromSetari(DIMENSIUNE_NUMAR_IDENTIFICATOR_PRODUS, DIMENSIUNE_NUMAR_IDENTIFICATOR_PRODUS_EXCEPTION);
        numarIdentificatorCol.setMinWidth(Double.parseDouble(dimensiuneNumarIdentificatorProdus));
        numarIdentificatorCol.setMaxWidth(Double.parseDouble(dimensiuneNumarIdentificatorProdus));

        TableColumn<TableRowComanda, String> numeColoana = (TableColumn<TableRowComanda, String>) createStringColumn(TableRowComanda.NUME_PRODUS, "Nume Produs", null, false);
        String dimensiuneNumeProdus = generalHelper.getSetareGeneralaFromSetari(DIMENISUNE_NUME_PRODUS, DIMENSIUNE_COLOANA_PRODUS_EXCEPTION);
        numeColoana.setMinWidth(Double.parseDouble(dimensiuneNumeProdus));
        numeColoana.setMaxWidth(Double.parseDouble(dimensiuneNumeProdus));

        TableColumn<TableRowComanda, Double> cantitateColoana = (TableColumn<TableRowComanda, Double>) createDoubleColumn(TableRowComanda.CANTITATE_PRODUS, "L", null, false);
        String dimensiuneCantitateProdus = generalHelper.getSetareGeneralaFromSetari(DIMENSIUNE_CANTITATE_PRODUS, DIMENSIUNE_COLOANA_CANTITATE_EXCEPTION);
        cantitateColoana.setMaxWidth(Double.parseDouble(dimensiuneCantitateProdus));
        cantitateColoana.setMinWidth(Double.parseDouble(dimensiuneCantitateProdus));

        tableView.getColumns().addAll(numarIdentificatorCol, numeColoana, cantitateColoana);

        for (String oras : generalHelper.getOraseFromSetari().keySet()) {
            TableColumn<TableRowComanda, String> orasCol = new TableColumn<>(oras);
            orasCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getListMap().get(oras)));
            tableView.getColumns().addAll(orasCol);
        }

        refreshComandaTabelWithData(tableView, business);
    }

    public void refreshComandaTabelWithData(TableView tableView, Business business) throws Exception {
        List<Product> productList = getSpecialSortedProdusList();

        List<Command> commandList = commandService.getCommandsAvailableInTableComanda(business);

        List<TableRowComanda> tableRowComandaList = new ArrayList<>();
        Set<String> oraseFromSetari = generalHelper.getOraseFromSetari().keySet();

        for (Product product : productList) {
            TableRowComanda tableRowComanda = new TableRowComanda();
            tableRowComanda.setIdentificatorProdus(product.getIdentificatorProdus());
            tableRowComanda.setNumeProdus(product.getNume());
            tableRowComanda.setCantitateProdus(product.getCantitate());

            Map<String, String> oraseCantitateMap = initializareMapOrasaCantiateMap(oraseFromSetari);

            for (Command command : commandList) {
                for (Integer produsIdFromComanda : command.getListaProduse().keySet()) {
                    if (produsIdFromComanda == product.getId()) {
                        Integer vecheaCantitateDacaExista = 0;
                        if (!oraseCantitateMap.get(command.getOras()).isEmpty()) {
                            vecheaCantitateDacaExista = Integer.valueOf(oraseCantitateMap.get(command.getOras()));
                        }
                        int nouaCantiate = vecheaCantitateDacaExista + command.getListaProduse().get(produsIdFromComanda);
                        oraseCantitateMap.put(command.getOras(), String.valueOf(nouaCantiate));
                    }
                }
            }

            tableRowComanda.setListMap(oraseCantitateMap);
            tableRowComandaList.add(tableRowComanda);
        }

        tableView.setItems(FXCollections.observableArrayList(tableRowComandaList));
    }

    /**
     * Product list shown in comanda table is sorted after order from setari
     */
    private List<Product> getSpecialSortedProdusList() throws Exception {
        List<Double> cantities = generalHelper.getCantitatiProdusDisponibile();
        List<Product> productListFromDb = productService.getAvailableProductsInCommandTable();
        List<Product> productList = new ArrayList<>();
        while(cantities.size()>0) {
            for (Product product : productListFromDb) {
                if(product.getCantitate()==cantities.get(0)){
                    productList.add(product);
                }
            }
            cantities.remove(0);
        }
        return productList;
    }

    private Map<String, String> initializareMapOrasaCantiateMap(Set<String> oraseFromSetari) throws GeneralExceptions {
        Map<String, String> oraseCantitateMap = new HashMap<>();
        for (String oras : oraseFromSetari) {
            oraseCantitateMap.put(oras, "");
        }
        return oraseCantitateMap;
    }


    public void constructProduseHeaderTable(TableView tableView, TextField textField) throws Exception {

        TableColumn<Product, String> identificatorCol = (TableColumn<Product, String>) createStringColumn(Product.IDENTIFICATOR_PRODUS, "Id produs", productService, true);
        TableColumn<Product, String> numeCol = (TableColumn<Product, String>) createStringColumn(Product.NUME, "Denumire", productService, true);

        TableColumn<Product, String> cantitateCol =  new TableColumn<>("Cantitate(L)");
        cantitateCol.setCellValueFactory(new PropertyValueFactory<>(Product.CANTITATE));
        cantitateCol.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(generalHelper.getCantitatiProdusDisponibileWithStrings())));
        cantitateCol.setOnEditCommit(event -> updateWithProperty(event.getTableView().getItems().get(event.getTablePosition().getRow()), Product.CANTITATE, Double.parseDouble(event.getNewValue()), productService));

        TableColumn<Product, String> existaInTableComenziCol = (TableColumn<Product, String>) createComboBoxColumn(Product.EXISTA_IN_TABEL_COMENZI, "Exista In Tabel Comenzi", trueFalseValues, productService);

        TableColumn<Product, TipProdus> tipProdusCol = new TableColumn<>("Tip");
        tipProdusCol.setCellValueFactory(new PropertyValueFactory<>(Product.TIP_PRODUS));
        ObservableList<TipProdus> tipProdusValues = FXCollections.observableArrayList(TipProdus.values());
        tipProdusCol.setCellFactory(ComboBoxTableCell.forTableColumn(tipProdusValues));
        tipProdusCol.setOnEditCommit(event -> updateProdusWithProperty(Product.TIP_PRODUS, event.getNewValue(), event.getTableView().getItems().get(event.getTablePosition().getRow())));

        refreshItemsFromProdusTableWithFilter(textField, tableView);

        tableView.getColumns().addAll(identificatorCol, numeCol, cantitateCol, tipProdusCol, existaInTableComenziCol);
    }

    /**
     * Used for filtering datas from table
     */
    public void refreshItemsFromProdusTableWithFilter(TextField textField, TableView tableView) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Product> filteredData = new FilteredList<>(FXCollections.observableArrayList(productService.getAll()), p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(produs -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                return produs.getNume().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Product> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator, Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }

    public void constructFirmeHeaderTable(TableView tableView, TextField textField) {
        TableColumn<Business, String> numeCol = (TableColumn<Business, String>) createStringColumn(Business.NUME, "Denumire", businessService, true);
        TableColumn<Business, String> cuiCol = (TableColumn<Business, String>) createStringColumn(Business.CUI, "CUI/CNP", businessService, true);
        TableColumn<Business, String> judetCol = (TableColumn<Business, String>) createStringColumn(Business.JUDET, "Judet", businessService, true);
        TableColumn<Business, String> sediulCol = (TableColumn<Business, String>) createStringColumn(Business.SEDIUL, "Sediul", businessService, true);
        TableColumn<Business, String> bancaCol = (TableColumn<Business, String>) createStringColumn(Business.MAIL, "Mail", businessService, true);
        TableColumn<Business, String> regiComCol = (TableColumn<Business, String>) createStringColumn(Business.REGCOM, "Reg. Com.", businessService, true);
        TableColumn<Business, String> comandaTableCol = (TableColumn<Business, String>) createComboBoxColumn(Business.COMANDA_TABEL, "Tabel", trueFalseValues, businessService);
        TableColumn<Business, String> existaInComenzi = (TableColumn<Business, String>) createComboBoxColumn(Business.EXISTA_IN_COMENZI, "Command", trueFalseValues, businessService);
        TableColumn<Business, String> lastResetTime = (TableColumn<Business, String>) createStringColumn(Business.LAST_RESET_TIME, "Ultimul timp de reset", businessService, false);

        TableColumn<Business, TipFirma> tipFirmaCol = new TableColumn<>("Tip");
        tipFirmaCol.setCellValueFactory(new PropertyValueFactory<>(Business.TIP_FIRMA));
        ObservableList<TipFirma> tipFirmaValues = FXCollections.observableArrayList(TipFirma.values());
        tipFirmaCol.setCellFactory(ComboBoxTableCell.forTableColumn(tipFirmaValues));
        tipFirmaCol.setOnEditCommit(event -> updateFirmaWithProperty(Business.TIP_FIRMA, event.getNewValue(), event.getTableView().getItems().get(event.getTablePosition().getRow())));

        refreshItemsFromFirmaTableWithFilter(textField, tableView);

        tableView.getColumns().addAll(numeCol, cuiCol, judetCol, sediulCol, bancaCol, regiComCol, tipFirmaCol, lastResetTime, comandaTableCol, existaInComenzi);
    }

    /**
     * Used for filtering datas from table Firme
     */
    public void refreshItemsFromFirmaTableWithFilter(TextField textField, TableView tableView) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Business> filteredData = new FilteredList<>(FXCollections.observableArrayList(businessService.getAll()), p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(firma -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                return firma.getNume().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Business> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator, Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }


    /**
     * String column
     */
    private TableColumn<?, String> createStringColumn(String proprietate, String numeColoana, BaseService baseService, boolean withUpdate) {
        TableColumn<?, String> column = new TableColumn<>(numeColoana);
        column.setCellValueFactory(new PropertyValueFactory<>(proprietate));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        if (withUpdate) {
            column.setOnEditCommit(event -> updateWithProperty((IEntity) event.getTableView().getItems().get(event.getTablePosition().getRow()), proprietate, event.getNewValue(), baseService));
        }
        return column;
    }


    /**
     * Double column
     */
    private TableColumn<?, Double> createDoubleColumn(String proprietate, String numeColoana, BaseService baseService, boolean withUpdate) {
        TableColumn<?, Double> column = new TableColumn<>(numeColoana);
        column.setCellValueFactory(new PropertyValueFactory<>(proprietate));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringTableConverter()));
        if (withUpdate) {
            column.setOnEditCommit(event -> updateWithProperty((IEntity) event.getTableView().getItems().get(event.getTablePosition().getRow()), proprietate, event.getNewValue(), baseService));
        }
        return column;
    }

    /**
     * Double column
     */
    private TableColumn<?, Double> createDoubleColumnPret(String proprietate, String numeColoana, BaseService baseService, boolean withUpdate, TextField textField, TableView tableView, Business business) {
        TableColumn<?, Double> column = new TableColumn<>(numeColoana);
        column.setCellValueFactory(new PropertyValueFactory<>(proprietate));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringTableConverter()));
        if (withUpdate) {
            column.setOnEditCommit(event -> updateWithPropertyPret((IEntity) event.getTableView().getItems().get(event.getTablePosition().getRow()), proprietate, event.getNewValue(), baseService, textField, tableView, business));
        }
        return column;
    }

    /**
     * ComboBox column
     */
    private TableColumn<?, String> createComboBoxColumn(String proprietate, String numeColoana, ObservableList<String> values, BaseService baseService) {
        TableColumn<?, String> column = new TableColumn<>(numeColoana);
        column.setCellValueFactory(new PropertyValueFactory<>(proprietate));
        column.setCellFactory(ComboBoxTableCell.forTableColumn(values));
        column.setOnEditCommit(event -> updateWithProperty((IEntity) event.getTableView().getItems().get(event.getTablePosition().getRow()), proprietate, event.getNewValue(), baseService));
        return column;
    }

    /**
     * Update string
     */
    private void updateWithProperty(IEntity model, String coloana, String newValue, BaseService baseService) {
        try {
            model.setField(coloana, newValue);
            baseService.update(model);
            LOGGER.debug("Updated model:" + model);
        } catch (PersistanceException | NoSuchFieldException | IllegalAccessException e) {
            alertWithError("Exista o entitate asemanatoare");
            LOGGER.error("Failed to update firma", e);
        }
    }

    /**
     * Update double
     */
    private void updateWithProperty(IEntity model, String coloana, Double newValue, BaseService baseService) {
        try {
            model.setField(coloana, newValue);
            baseService.update(model);
            LOGGER.debug("Updated model:" + model);
        } catch (PersistanceException | NoSuchFieldException | IllegalAccessException e) {
            alertWithError("Exista o entitate asemanatoare");
            LOGGER.error("Failed to update firma", e);
        }
    }

    /**
     * Update double
     */
    private void updateWithPropertyPret(IEntity model, String coloana, Double newValue, BaseService baseService, TextField textField, TableView tableView, Business business) {
        try {
            model.setField(coloana, newValue);
            baseService.update(model);
            refreshItemsFromPreturiTable(textField, tableView, business);
            LOGGER.debug("Updated model:" + model);
        } catch (PersistanceException | NoSuchFieldException | IllegalAccessException e) {
            alertWithError("Exista o entitate asemanatoare");
            LOGGER.error("Failed to update business", e);
        }
    }

    /**
     * Update LocalDAte
     */
    private void updateWithProperty(IEntity model, String coloana, LocalDate newValue, BaseService baseService) {
        try {
            model.setField(coloana, newValue);
            baseService.update(model);
            LOGGER.debug("Updated model:" + model);
        } catch (PersistanceException | NoSuchFieldException | IllegalAccessException e) {
            alertWithError("Exista o entitate asemanatoare");
            LOGGER.error("Failed to update firma", e);
        }
    }


    /**
     * Update enum for Business
     */
    private void updateFirmaWithProperty(String coloana, TipFirma newValue, Business business) {
        try {
            business.setField(coloana, newValue);
            businessService.update(business);
            LOGGER.debug("Updated business:" + business);
        } catch (PersistanceException | NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Failed to update business", e);
        }
    }

    /**
     * Update enum for Product
     */
    private void updateProdusWithProperty(String coloana, TipProdus newValue, Product product) {
        try {
            product.setField(coloana, newValue);
            productService.update(product);
            LOGGER.debug("Updated product:" + product);
        } catch (PersistanceException | NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Failed to update product", e);
        }
    }

    public double getTotalCantitty(Command command, List<Product> productList) {
        double totalCantity = 0;

        for (Product product : productList) {
            Integer cantitate = command.getListaProduse().get(product.getId());
            totalCantity += product.getCantitate() * cantitate;
        }

        return totalCantity;
    }
}

