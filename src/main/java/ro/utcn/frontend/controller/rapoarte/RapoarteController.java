package ro.utcn.frontend.controller.rapoarte;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.backend.backendservices.BusinessService;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.frontend.frontendServices.PopUpService;
import ro.utcn.helper.JavaFxComponentsHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Rapoarte
 * <p>
 * Created by Lucian on 5/9/2017.
 */

@Component
public class RapoarteController extends GeneralActions implements Initializable {

    public static final String ALL = "Toate";

    @FXML
    public AnchorPane rapoarteAnchorPane;

    @Autowired
    private BusinessService businessService;
    @Autowired
    private PopUpService popUpService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label labelFrom = JavaFxComponentsHelper.createLabel(15, 40, 14, "Alege data de la:");
        DatePicker datePickerFrom = JavaFxComponentsHelper.createDatePicker(150, 40);

        Label labelTo = JavaFxComponentsHelper.createLabel(15, 70, 14, "Alege data pana la:");
        DatePicker datePickerTo = JavaFxComponentsHelper.createDatePicker(150, 70);

        Label firmaLabel = JavaFxComponentsHelper.createLabel(15, 100, 14, "Firma:");
        ChoiceBox<String> choiceBoxFirma = JavaFxComponentsHelper.createChoiceBox(150,100);

        List<String> stringList = new ArrayList<>();
        stringList.add(ALL);
        businessService.getAll().forEach(e-> stringList.add(e.getNume()));
        choiceBoxFirma.setItems(FXCollections.observableArrayList(stringList));

        Button produseVandute = JavaFxComponentsHelper.createButton("Cate produse am vandut?", 15, 135);
        produseVandute.getStyleClass().add("buttonClass");
        produseVandute.setOnAction(event -> popUpService.shouProductsSold(datePickerFrom, datePickerTo, choiceBoxFirma));

        rapoarteAnchorPane.getChildren().addAll(labelFrom, labelTo, datePickerFrom, datePickerTo, firmaLabel, choiceBoxFirma, produseVandute);
    }


}
