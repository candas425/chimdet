package ro.utcn.frontend.controller.setari;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.backend.backendservices.GeneralSettingService;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.GeneralSetting;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.frontend.frontendServices.InitializeService;

import java.net.URL;
import java.util.ResourceBundle;

import static ro.utcn.Constants.*;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * Created by Lucas on 4/17/2017.
 */

@Component
public class SetariController extends GeneralActions implements Initializable {

    public Button saveButton;
    public TextField textFieldCriteriuFisier;
    public TextField textFieldContorFisierPanaLaCantitate;
    public TextArea textAreaListPrefixeFisiereExcel;
    public TextField textFieldParametruScalareX;
    public TextField textFieldParametruScalareY;
    public TextField textFieldParametruTranslatareX;
    public TextField textFieldParametruTranslatareY;
    public TextField textFieldParametruInaltimeTabel;
    public TextField textFieldParametruTVA;
    public TextField textFieldDimensiuneNume;
    public TextField textFieldDimensiuneCantitate;
    public TextField texFieldCantitatiDisponibile;
    public TextField textFieldDimensiuneNumarIdentificator;
    public TextArea textAreaContinutEmail;

    private Logger LOGGER = LogManager.getLogger(SetariController.class);

    public AnchorPane setariAnchorPane;

    @Autowired
    private InitializeService initializeService;
    @Autowired
    private GeneralSettingService generalSettingService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeService.loadInitialSetariPage(saveButton, textFieldCriteriuFisier, textFieldContorFisierPanaLaCantitate,
                textAreaListPrefixeFisiereExcel, textFieldParametruScalareX, textFieldParametruScalareY,
                textFieldParametruTranslatareX, textFieldParametruTranslatareY, textFieldParametruInaltimeTabel,
                textFieldParametruTVA, textFieldDimensiuneNume, textFieldDimensiuneCantitate, texFieldCantitatiDisponibile,
                textFieldDimensiuneNumarIdentificator, textAreaContinutEmail);
    }

    //Salveaza toate setarile generale
    public void salveazaSetari() {
        int contor = 0;

        //text areas
        contor += validateAndCreateSetareGeneralaForTextArea(textAreaListPrefixeFisiereExcel, LISTA_INITIALE_FISIERE_EXCEL);
        contor += validateAndCreateSetareGeneralaForTextArea(textAreaContinutEmail, CONTINUT_EMAIL);

        //text fields
        contor += validateAndCreateSetareGeneralaForTextField(textFieldCriteriuFisier, CRITERIU_CAUTARE_FISIER_EXCEL);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldContorFisierPanaLaCantitate, CONTOR_FISIER_PANA_LA_CANTITATE);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldParametruScalareX, PARAMETRU_SCALARE_X);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldParametruScalareY, PARAMETRU_SCALARE_Y);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldParametruTranslatareX, PARAMETRU_TRANSLATARE_X);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldParametruTranslatareY, PARAMETRU_TRANSLATARE_Y);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldParametruInaltimeTabel, PARAMETRU_TABEL_HEIGHT);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldParametruTVA, VALOARE_TVA);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldDimensiuneNume, DIMENISUNE_NUME_PRODUS);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldDimensiuneCantitate, DIMENSIUNE_CANTITATE_PRODUS);
        contor += validateListaCantitatiDisponibile(texFieldCantitatiDisponibile, LISTA_CANTITATI_PRODUS_DISPONIBILE);
        contor += validateAndCreateSetareGeneralaForTextField(textFieldDimensiuneNumarIdentificator, DIMENSIUNE_NUMAR_IDENTIFICATOR_PRODUS);

        if (contor == 0) {
            alertWithDetails("Setarile au fost salvate");
        } else {
            alertWithError("Completeaza toate campurile");
        }
    }

    /**
     * Used for validating and creating a GeneralSetting for TextField
     */
    private int validateAndCreateSetareGeneralaForTextField(TextField textField, String property) {
        if (textField.getText().isEmpty() || !createAndSaveSetareGenerala(property, textField.getText())) {
            return 1;
        }
        return 0;
    }

    /**
     * Used for validating and creating a GeneralSetting for TextArea
     */
    private int validateAndCreateSetareGeneralaForTextArea(TextArea textArea, String property) {
        if (textArea.getText().isEmpty() || !createAndSaveSetareGenerala(property, textArea.getText())) {
            return 1;
        }
        return 0;
    }

    /**
     * Used for special case for lista de cantitati
     */
    private int validateListaCantitatiDisponibile(TextField textField, String property) {
        String[] list = textField.getText().split(",");
        for (String s : list) {
            try {
                double result = Double.parseDouble(s);
            } catch (Exception e) {
                return 1;
            }
        }
        if (textField.getText().isEmpty() || !createAndSaveSetareGenerala(property, textField.getText())) {
            return 1;
        }
        return 0;
    }


    /**
     * Used for creating and saving setare generala
     */
    private boolean createAndSaveSetareGenerala(String numeProprietate, String valoareProprietate) {
        GeneralSetting generalSetting = new GeneralSetting();
        generalSetting.setNumeProprietate(numeProprietate);
        generalSetting.setValoareProprietate(valoareProprietate);
        try {
            generalSettingService.saveGeneralSetting(generalSetting);
            return true;
        } catch (PersistanceException e) {
            LOGGER.error(e);
            alertWithError("Setarile generale nu au putut fi salvate");
        }
        return false;
    }

}
