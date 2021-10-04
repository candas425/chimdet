package ro.utcn.helper;

import javafx.scene.control.TextFormatter;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.backend.model.Holiday;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.backend.model.GeneralSetting;
import ro.utcn.backend.backendservices.GeneralSettingService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ro.utcn.Constants.LISTA_CANTITATI_PRODUS_DISPONIBILE;
import static ro.utcn.Constants.LISTA_INITIALE_FISIERE_EXCEL;
import static ro.utcn.exceptions.GeneralExceptions.LISTA_CANTITATI_PRODUSE_DISPONIBILE_EXCEPTION;
import static ro.utcn.exceptions.GeneralExceptions.LISTA_DE_ORASE_NU_ESTE_SETATA_IN_SETARI;


/**
 * Helper general
 * Created by Lucian on 4/21/2017.
 */

@Component
public class GeneralHelper {

    @Autowired
    private GeneralSettingService generalSettingService;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final String WHITE_SPACE = " ";
    public static final String EMPTY_SPACE = "";

    public Holiday getHolidayWithDate(List<Holiday> holidayList, LocalDate localDate) {
        for (Holiday holiday : holidayList) {
            if (holiday.getData().equals(localDate)) {
                return holiday;
            }
        }
        return null;
    }

    /**
     * Used for allowing only numbers to be introduced in textfields
     */
    public TextFormatter<String> getNumberTextFieldFormatter() {
        return new TextFormatter<>(change -> {
            change.setText(change.getText().replaceAll("[^0-9.,-]", ""));
            return change;
        });
    }

    /**
     * Used for getting available cantities for products in double List
     */
    public List<Double> getCantitatiProdusDisponibile() throws Exception {
        GeneralSetting generalSettingListaCantitati = generalSettingService.getGeneralSetting(LISTA_CANTITATI_PRODUS_DISPONIBILE);
        if (generalSettingListaCantitati == null) {
            throw new GeneralExceptions(LISTA_CANTITATI_PRODUSE_DISPONIBILE_EXCEPTION);
        }
        List<Double> resultList = new ArrayList<>();
        String[] list = generalSettingListaCantitati.getValoareProprietate().split(",");

        for (String s : list) {
            try {
                Double valoare = Double.parseDouble(s);
                resultList.add(valoare);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }

        return resultList;
    }

    /**
     * Used for getting available cantities for products in string list
     */
    public List<String> getCantitatiProdusDisponibileWithStrings() throws Exception {
        List<Double> doubleList = getCantitatiProdusDisponibile();
        Collections.sort(doubleList);

        List<String> stringList = new ArrayList<>();
        for(Double d: doubleList){
            stringList.add(String.valueOf(d));
        }

        return stringList;
    }

    public Map<String, String> getOraseFromSetari() throws GeneralExceptions, IOException {
        GeneralSetting generalSettingLista = generalSettingService.getGeneralSetting(LISTA_INITIALE_FISIERE_EXCEL);
        if (generalSettingLista == null) {
            throw new GeneralExceptions(LISTA_DE_ORASE_NU_ESTE_SETATA_IN_SETARI);
        }
        Map<String, String> mapOrase = new HashMap<>();
        String[] listaOraseDinFisier = StringHelper.split(";= \n", generalSettingService.getGeneralSetting(LISTA_INITIALE_FISIERE_EXCEL).getValoareProprietate());
        for (int i = 0; i < listaOraseDinFisier.length; i++) {
            if (i % 2 != 0) {
                mapOrase.put(listaOraseDinFisier[i - 1], listaOraseDinFisier[i]);
            }
        }

        return mapOrase;
    }

    public String getSetareGeneralaFromSetari(String field, String exceptie) throws GeneralExceptions {
        GeneralSetting generalSetting = generalSettingService.getGeneralSetting(field);
        if (generalSetting == null) {
            throw new GeneralExceptions(exceptie);
        }
        return generalSetting.getValoareProprietate();
    }

    public String getLunaInRomana(int luna) {
        switch (luna) {
            case 1:
                return "Ianuarie";
            case 2:
                return "Februarie";
            case 3:
                return "Martie";
            case 4:
                return "Aprilie";
            case 5:
                return "Mai";
            case 6:
                return "Iunie";
            case 7:
                return "Iulie";
            case 8:
                return "August";
            case 9:
                return "Septembrie";
            case 10:
                return "Octombrie";
            case 11:
                return "Noiembrie";
            case 12:
                return "Decembrie";
            default:
                return "";
        }
    }

    public static void mouseDragEnter(final DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    // Method to to get extension of a file
    public static String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return fileName.substring(i + 1).toLowerCase();
    }

    public static int getCantity(Map<Double, Integer> integerIntegerMap) {
        int cantity = 0;
        for (Map.Entry<Double, Integer> map : integerIntegerMap.entrySet()) {
            cantity += map.getValue();
        }
        return cantity;
    }

}
