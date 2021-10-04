package ro.utcn.frontend.frontendServices;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.utcn.Manager;
import ro.utcn.backend.backendservices.EmployeeService;
import ro.utcn.backend.backendservices.GeneralSettingService;
import ro.utcn.backend.model.*;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.helper.WorklogEmployee;
import ro.utcn.frontend.frontendServices.helper.ListRowComanda;
import ro.utcn.frontend.importer.ExcelCondicaParser;
import ro.utcn.helper.GeneralHelper;
import ro.utcn.helper.JavaFxComponentsHelper;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static ro.utcn.Constants.*;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * Initializare pagini
 * <p>
 * Created by Lucian on 4/2/2017.
 */

@Service
public class InitializeService {

    @Resource(name = "windowWidth")
    private int windowWidth;

    @Resource(name = "windowHeight")
    private int windowHeight;

    private Logger LOGGER = LogManager.getLogger(InitializeService.class);

    @Autowired
    private Manager manager;
    @Autowired
    private TableService tableService;
    @Autowired
    private ListViewService listViewService;
    @Autowired
    private GeneralSettingService generalSettingService;
    @Autowired
    private GeneralHelper generalHelper;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ExcelCondicaParser excelCondicaParser;

    public void loadLogo(ImageView imageLogo) {
        Image image = new Image("images/logo.png");
        imageLogo.setImage(image);
    }

    public void loadVersionLogo(ImageView imageLogo) {
        Image image = new Image("images/version.png");
        imageLogo.setImage(image);
    }

    public void loadInitialConcediuPage(TableView<Holiday> tableView, AnchorPane anchorPane, TextField textField, Employee employee) {
        loadTableProperties(tableView, 65, 15, windowWidth, windowHeight, false);
        tableService.constructHolidayTable(tableView, textField, employee);
        anchorPane.getChildren().add(tableView);
    }

    public void loadInitialHolidayPage(TableView<Holiday> tableView, AnchorPane anchorPane, TextField textField) {
        loadTableProperties(tableView, 65, 15, windowWidth, windowHeight, false);
        tableService.constructHolidayTable(tableView, textField, null);
        anchorPane.getChildren().add(tableView);
    }

    public void loadInitialPontajPage(TableView tableView, AnchorPane anchorPane, Label labelLuna) throws GeneralExceptions {
        loadTableProperties(tableView, 87, 15, windowWidth, windowHeight - 30, true);
        Label label = JavaFxComponentsHelper.createLabel(329, 34, 14, "Alege data:");
        DatePicker datePicker = JavaFxComponentsHelper.createDatePicker(415, 33);
        Button button = JavaFxComponentsHelper.createButton("Regenereaza pontaj", 593, 30);
        button.getStyleClass().add("buttonClass");
        Button buttonCondica = JavaFxComponentsHelper.createButton("Condica", 733, 30);
        buttonCondica.getStyleClass().add("buttonClass");
        labelLuna = JavaFxComponentsHelper.createLabel(windowWidth / 2 - 80, 63, 14, generalHelper.getLunaInRomana(LocalDate.now().getMonth().getValue()) + " - " + LocalDate.now().getYear());

        Label finalLabelLuna = labelLuna;
        button.setOnAction(event -> {
            if (datePicker.getValue() != null) {
                tableView.getItems().clear();
                tableView.getColumns().clear();

                LocalDate localDate = datePicker.getValue();
                tableService.constructPontajTable(tableView, localDate);

                finalLabelLuna.setText(generalHelper.getLunaInRomana(localDate.getMonthValue()) + " - " + localDate.getYear());
            } else {
                alertWithError("Alege o data");
            }
        });
        buttonCondica.setOnAction(event -> {
            if (datePicker.getValue() != null) {
                LocalDate localDate = datePicker.getValue();
                List<WorklogEmployee> worklogEmployeeList = employeeService.getEmployeeListForCondica(localDate);
                try {
                    excelCondicaParser.parseFile(worklogEmployeeList, generalHelper.getLunaInRomana(localDate.getMonthValue()), String.valueOf(localDate.getYear()));

                    alertWithDetails("Fisierul a fost salvat in folderul de 'files' si are numele de condicaResult.xlsx, si acum se printeaza...");
                } catch (IOException | GeneralExceptions e) {
                    alertWithError("Eroare la generarea fisierului de condica:" + e.getMessage());
                }

            } else {
                alertWithError("Alege o data");
            }
        });

        tableService.constructPontajTable(tableView, LocalDate.now());

        anchorPane.getChildren().addAll(labelLuna, label, button, buttonCondica, datePicker, tableView);
    }

    public void loadInitialAngajatiPage(AnchorPane anchorPane, TableView<Employee> tableView, TextField filterField) {
        loadTableProperties(tableView, 65, 15, windowWidth, windowHeight, true);
        tableService.constructAngajatiTable(tableView, filterField);
        anchorPane.getChildren().add(tableView);
    }

    public void loadInitialComandaListaPage(AnchorPane anchorPane, ListView<Product> listView, ListView<ListRowComanda> secondListView, Label textCautat) {
        loatListViewProperties(listView, 90, 16, windowHeight - 140, windowWidth / 2 - 100);
        loatListViewProperties(secondListView, 60, windowWidth / 2 + 20, windowHeight / 2, windowWidth / 2 - 100);
        listViewService.constructComandaListaList(listView, secondListView, textCautat);
        anchorPane.getChildren().add(listView);
        anchorPane.getChildren().add(secondListView);
    }

    public void loadInitialComandaIstoricPage(TableView<Command> tableView, AnchorPane anchorPane, Business business, TextField filterField) {
        loadTableProperties(tableView, 70, 15, windowWidth, windowHeight, true);
        tableService.constructComandaIstoricTable(tableView, business, filterField);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        anchorPane.getChildren().add(tableView);
    }

    public void loadInitialSetariPage(Button saveButton, TextField textFieldCriteriuFisier, TextField textFieldContorFisier,
                                      TextArea textAreaListPrefixeFisiereExcel, TextField textFieldParametruScalareX,
                                      TextField textFieldParametruScalareY, TextField textFieldParametruTranslatareX,
                                      TextField textFieldParametruTranslatareY, TextField textFieldParametruInaltimeTabel,
                                      TextField textFieldParametruTVA, TextField textFieldDimensiuneNume,
                                      TextField textFieldDimensiuneCantitate, TextField textFieldCantitatiDisponibile,
                                      TextField textFieldDimensiuneNumarIdentificator,
                                      TextArea textAreaContinutMail) {

        //buton Salvare Setari
        saveButton.setLayoutX((windowWidth - 140) / 2);
        saveButton.setLayoutY(windowHeight - 100);

        //nummber formatter
        textFieldContorFisier.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldParametruScalareX.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldParametruScalareY.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldParametruTranslatareX.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldParametruTranslatareY.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldParametruInaltimeTabel.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldParametruTVA.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldDimensiuneNume.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldDimensiuneCantitate.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldCantitatiDisponibile.setTextFormatter(generalHelper.getNumberTextFieldFormatter());
        textFieldDimensiuneNumarIdentificator.setTextFormatter(generalHelper.getNumberTextFieldFormatter());

        //text areas
        setTextAreaValue(LISTA_INITIALE_FISIERE_EXCEL, textAreaListPrefixeFisiereExcel);
        setTextAreaValue(CONTINUT_EMAIL, textAreaContinutMail);

        //text fields
        setTextFieldValue(CRITERIU_CAUTARE_FISIER_EXCEL, textFieldCriteriuFisier);
        setTextFieldValue(CONTOR_FISIER_PANA_LA_CANTITATE, textFieldContorFisier);
        setTextFieldValue(PARAMETRU_SCALARE_X, textFieldParametruScalareX);
        setTextFieldValue(PARAMETRU_SCALARE_Y, textFieldParametruScalareY);
        setTextFieldValue(PARAMETRU_TRANSLATARE_X, textFieldParametruTranslatareX);
        setTextFieldValue(PARAMETRU_TRANSLATARE_Y, textFieldParametruTranslatareY);
        setTextFieldValue(PARAMETRU_TABEL_HEIGHT, textFieldParametruInaltimeTabel);
        setTextFieldValue(VALOARE_TVA, textFieldParametruTVA);
        setTextFieldValue(DIMENISUNE_NUME_PRODUS, textFieldDimensiuneNume);
        setTextFieldValue(DIMENSIUNE_CANTITATE_PRODUS, textFieldDimensiuneCantitate);
        setTextFieldValue(LISTA_CANTITATI_PRODUS_DISPONIBILE, textFieldCantitatiDisponibile);
        setTextFieldValue(DIMENSIUNE_NUMAR_IDENTIFICATOR_PRODUS, textFieldDimensiuneNumarIdentificator);
    }

    /**
     * Used for setting initial textfield value for setari page
     */
    private void setTextFieldValue(String property, TextField textField) {
        GeneralSetting generalSetting = generalSettingService.getGeneralSetting(property);
        if (generalSetting != null) {
            textField.setText(generalSetting.getValoareProprietate());
        }
    }

    /**
     * Used for setting initial textArea value for setari page
     */
    private void setTextAreaValue(String property, TextArea textArea) {
        GeneralSetting generalSetting = generalSettingService.getGeneralSetting(property);
        if (generalSetting != null) {
            textArea.setText(generalSetting.getValoareProprietate());
        }
    }

    public void loadInitialProdusePage(TableView tableView, AnchorPane anchorPane, TextField textField) throws Exception {
        loadTableProperties(tableView, 65, 15, windowWidth, windowHeight, true);
        tableService.constructProduseHeaderTable(tableView, textField);
        anchorPane.getChildren().add(tableView);
    }

    public void loadInitialFirmePage(TableView tableView, AnchorPane anchorPane, TextField textField) {
        loadTableProperties(tableView, 65, 16, windowWidth, windowHeight, true);
        tableService.constructFirmeHeaderTable(tableView, textField);
        anchorPane.getChildren().add(tableView);
    }

    public void loadInitialPreturiPage(TableView<Price> tableView, AnchorPane anchorPane, TextField textField, Business business) throws GeneralExceptions {
        loadTableProperties(tableView, 70, 16, windowWidth, windowHeight, true);
        tableService.constructPreturiTable(tableView, textField, business);
        anchorPane.getChildren().add(tableView);
    }

    public void loadInitialComenziTablePage(TableView table, AnchorPane anchorPane, Business business) throws Exception {
        loadTableProperties(table, 67, 15, windowWidth, windowHeight, false);
        tableService.constructComandaTableHeaderTable(table, business);
        table.getStyleClass().add("tableView");
        anchorPane.getChildren().add(table);
    }


    public void loadInitialComenziPage(List<Business> businessList, AnchorPane anchorPane) {
        if (businessList != null) {

            int initialX = 25;
            int initialY = 50;
            for (Business business : businessList) {
                Button button = constructButton(business.getNume(), initialY, initialX);
                if (business.getComandaTabel() != null && business.getComandaTabel().equals(TRUE)) {
                    button.setOnAction(event -> {
                        try {
                            manager.setBusinessFromComandaTabel(business);
                            manager.loadComandaTabelPage();
                        } catch (IOException e) {
                            LOGGER.error("Failed to load Comenzi Page", e);
                        }
                    });
                } else {
                    button.setOnAction(event -> {
                        try {
                            manager.setBusinessFromComandaTabel(business);
                            manager.loadComandaListaPage();
                        } catch (IOException e) {
                            LOGGER.error("Failed to load Lista Page", e);
                        }
                    });
                }

                anchorPane.getChildren().add(button);
                initialX += 195;
                if (initialX > windowWidth - 30) {
                    initialY += 80;
                    initialX = 25;
                }
            }
        }
    }


    private void loadTableProperties(TableView tableView, int layoutY, int layoutX, double windowWidth, double windowHeight, boolean editable) {
        tableView.setEditable(editable);
        tableView.setLayoutY(layoutY);
        tableView.setLayoutX(layoutX);
        tableView.setPrefWidth(windowWidth - 50);
        tableView.setPrefHeight(windowHeight - 120);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loatListViewProperties(ListView listView, int layoutY, int layoutX, int prefHeight, int prefWidth) {
        listView.setLayoutX(layoutX);
        listView.setLayoutY(layoutY);
        listView.setPrefWidth(prefWidth);
        listView.setPrefHeight(prefHeight);
    }


    private Button constructButton(String nume, int initialY, int initialX) {
        Button button = new Button(nume);
        button.setLayoutY(initialY);
        button.setLayoutX(initialX);
        button.setPrefWidth(150);
        button.setPrefHeight(50);
        button.getStyleClass().add("buttonClass");
        return button;
    }
}
