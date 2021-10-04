package ro.utcn.helper;

import javafx.collections.FXCollections;
import javafx.print.*;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.helper.TableRowComanda;

import javax.annotation.Resource;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ro.utcn.Constants.*;
import static ro.utcn.Constants.PARAMETRU_TABEL_HEIGHT;
import static ro.utcn.exceptions.GeneralExceptions.*;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;


/**
 * Helper pentru componente vizuale
 * <p>
 * Created by Lucian on 5/9/2017.
 */
@Component
public class JavaFxComponentsHelper {

    @Resource(name = "windowWidth")
    private int windowWidth;

    @Resource(name = "windowHeight")
    private int windowHeight;

    @Autowired
    protected GeneralHelper generalHelper;

    public static DatePicker createDatePicker(int layoutX, int layoutY) {
        DatePicker datePicker = new DatePicker();
        datePicker.setLayoutY(layoutY);
        datePicker.setLayoutX(layoutX);
        return datePicker;
    }

    public static Button createButton(String text, int layoutX, int layoutY) {
        Button button = new Button(text);
        button.setLayoutY(layoutY);
        button.setLayoutX(layoutX);
        return button;
    }

    public static ChoiceBox<String> createChoiceBox(int layoutX, int layoutY) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setLayoutX(layoutX);
        choiceBox.setLayoutY(layoutY);
        return choiceBox;
    }

    public static Label createLabel(int layoutX, int layoutY, int fontSize, String textToAppend) {
        Label label = new Label();
        label.setText(textToAppend);
        label.setLayoutX(layoutX);
        label.setLayoutY(layoutY);
        label.setFont(Font.font(null, FontWeight.BOLD, fontSize));
        return label;
    }

    public void printEverything(TableView<TableRowComanda> tabel) throws GeneralExceptions {
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);

        String setareGeneralaParamScaX;
        String setareGeneralaParamScaY;
        String setareGeneralaParamTraX;
        String setareGeneralaParamTraY;
        String setareGeneralaTableHeight;

        try {
            setareGeneralaParamScaX = generalHelper.getSetareGeneralaFromSetari(PARAMETRU_SCALARE_X, PARAMETRU_SCALARE_X_EXCEPTION);
            setareGeneralaParamScaY = generalHelper.getSetareGeneralaFromSetari(PARAMETRU_SCALARE_Y, PARAMETRU_SCALARE_Y_EXCEPTION);
            setareGeneralaParamTraX = generalHelper.getSetareGeneralaFromSetari(PARAMETRU_TRANSLATARE_X, PARAMETRU_TRANSLATARE_X_EXCEPTION);
            setareGeneralaParamTraY = generalHelper.getSetareGeneralaFromSetari(PARAMETRU_TRANSLATARE_Y, PARAMETRU_TRANSLATARE_Y_EXCEPTION);
            setareGeneralaTableHeight = generalHelper.getSetareGeneralaFromSetari(PARAMETRU_TABEL_HEIGHT, PARAMETRU_TABEL_HEIGHT_EXCEPTION);
        } catch (GeneralExceptions generalExceptions) {
            throw new GeneralExceptions(generalExceptions.getMessage());
        }

        List<TableRowComanda> availableTableRowComanda = new ArrayList<>();
        availableTableRowComanda.addAll(tabel.getItems());

        tabel.setItems(FXCollections.observableArrayList(availableTableRowComanda));

        tabel.setScaleX(Double.parseDouble(setareGeneralaParamScaX));
        tabel.setScaleY(Double.parseDouble(setareGeneralaParamScaY));
        tabel.setTranslateX(-Double.parseDouble(setareGeneralaParamTraX));
        tabel.setTranslateY(-Double.parseDouble(setareGeneralaParamTraY));
        tabel.setPrefHeight(windowHeight + Double.parseDouble(setareGeneralaTableHeight));

        PrinterJob job = PrinterJob.createPrinterJob(printer);
        boolean success = job.printPage(pageLayout, tabel);

        tabel.setTranslateX(0);
        tabel.setTranslateY(0);
        tabel.setScaleX(1.0);
        tabel.setScaleY(1.0);
        tabel.setPrefHeight(windowHeight - 50);
        if (success) {
            job.endJob();
        } else {
            throw new GeneralExceptions(PRINT_FAILED);
        }
        throw new GeneralExceptions(PRINT_SUCCESS);
    }

    public static void printEverythingWithText(String message) throws GeneralExceptions {
        GridPane gridPane = new GridPane();
        Label textArea = new Label();
        textArea.setText(message);
        gridPane.getChildren().add(textArea);

        Printer printer = Printer.getDefaultPrinter();
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
        boolean success = job.printPage(pageLayout, gridPane);

        if (success) {
            job.endJob();
            throw new GeneralExceptions(PRINT_SUCCESS);
        } else {
            throw new GeneralExceptions(PRINT_FAILED);
        }
    }

    public static void printFile(String filePath) throws IOException {
        File fileToPrint = new File(filePath);
        Desktop.getDesktop().print(fileToPrint);
    }


    public static void fileExporter(Workbook workbook, Stage stage) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel file", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                FileHelper.saveFile(workbook, file);
                alertWithDetails("Fisierul a fost salvat");
            } catch (Exception e) {
                alertWithError(e.getMessage());
            }
        }
    }
}
