package ro.utcn.frontend.importer;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import ro.utcn.backend.model.Business;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.helper.ComandaHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static ro.utcn.Constants.CONTOR_FISIER_PANA_LA_CANTITATE;
import static ro.utcn.Constants.CRITERIU_CAUTARE_FISIER_EXCEL;
import static ro.utcn.exceptions.GeneralExceptions.SETEAZA_UN_CONTOR_PANA_LA_CANTIATE_IN_SETARI;
import static ro.utcn.exceptions.GeneralExceptions.SETEAZA_UN_CRITERIU_DE_CAUTRARE_IN_SETARI;

/**
 * Used for parsing excel file
 * <p>
 * Created by Lucian on 4/20/2017.
 */

@Component
public class ExcelParser extends FileParser {

    @Override
    public void parseFileAndSave(File file, Business business, TableView tableView, ListView listView) throws IOException, GeneralExceptions {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet firstSheet = workbook.getSheetAt(0);
                Iterator<Row> iterator = firstSheet.iterator();

                String setareGeneralaCriteriu = generalHelper.getSetareGeneralaFromSetari(CRITERIU_CAUTARE_FISIER_EXCEL, SETEAZA_UN_CRITERIU_DE_CAUTRARE_IN_SETARI);
                String setareGeneralaContor = generalHelper.getSetareGeneralaFromSetari(CONTOR_FISIER_PANA_LA_CANTITATE, SETEAZA_UN_CONTOR_PANA_LA_CANTIATE_IN_SETARI);

                Map<String, Double> stringDoubleMap = readFromExcelFile(setareGeneralaCriteriu, Integer.parseInt(setareGeneralaContor), iterator);

                String oras = getOras(file.getName());

                ComandaHelper comandaHelper = parseProductCodeAndCantity(stringDoubleMap, true);

                createAndSaveComanda(comandaHelper.getProdusIntegerMap(), business, oras, tableView, file.getName(), true);

                if (comandaHelper.getErrorMessage().length() > 1) {
                    throw new GeneralExceptions("Command a fost salvata dar:\n" + comandaHelper.getErrorMessage());
                }
            }
        }
    }

    private Map<String, Double> readFromExcelFile(String criteriuDeCautare, int counterToCantitateFromSetting, Iterator<Row> iterator) {
        boolean start = false;
        boolean nextCellIndexCode = false;
        int counterToCantitate = counterToCantitateFromSetting;
        Map<String, Double> productCodeAndCantity = new HashMap<>();
        String codProdus = "";

        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            int ok = 0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                String cellValueString = null;
                Double cellValueDouble = null;

                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        cellValueString = cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        cellValueDouble = cell.getNumericCellValue();
                        break;
                }


                if (cellValueString != null && cellValueString.contains(criteriuDeCautare)) {
                    start = true;
                }

                if (start && nextCellIndexCode && cellValueString != null) {
                    nextCellIndexCode = false;
                    counterToCantitate = counterToCantitateFromSetting;
                    //aici am obtinut codul de identificare a produsului
                    codProdus = cellValueString;
                }

                if (start && cellValueDouble != null) {
                    if (ok == 0) {
                        nextCellIndexCode = true;
                    }
                    ok++;
                    counterToCantitate--;
                    if (counterToCantitate == 0) {
                        ok = 0;
                        //aici am obtinut cantitatea produsului
                        double cantitateExistenta = productCodeAndCantity.get(codProdus) != null ? productCodeAndCantity.get(codProdus):0;
                        productCodeAndCantity.put(codProdus, cantitateExistenta + cellValueDouble);
                        codProdus = "";
                    }
                }
            }
        }

        return productCodeAndCantity;
    }


}
