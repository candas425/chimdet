package ro.utcn.frontend.importer;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.hibernate.internal.util.StringHelper;
import org.springframework.stereotype.Component;
import ro.utcn.backend.model.Business;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.helper.ComandaHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ro.utcn.Constants.CRITERIU_CAUTARE_FISIER_EXCEL;
import static ro.utcn.exceptions.GeneralExceptions.SETEAZA_UN_CRITERIU_DE_CAUTRARE_IN_SETARI;
import static ro.utcn.helper.GeneralHelper.EMPTY_SPACE;
import static ro.utcn.helper.GeneralHelper.WHITE_SPACE;

/**
 * Used for parsing pdf files
 * <p>
 * Created by Lucian on 5/20/2017.
 */

@Component
public class PDFParser extends FileParser {

    @Override
    public void parseFileAndSave(File file, Business business, TableView tableView, ListView listView) throws IOException, GeneralExceptions {
        //read content
        PDDocument document = PDDocument.load(file);
        PDFTextStripper s = new PDFTextStripper();
        String content = s.getText(document);

        String setareGeneralaCriteriu = generalHelper.getSetareGeneralaFromSetari(CRITERIU_CAUTARE_FISIER_EXCEL, SETEAZA_UN_CRITERIU_DE_CAUTRARE_IN_SETARI);

        Map<String, Double> stringDoubleMap = readFromPDFFile(content, setareGeneralaCriteriu);

        String oras;
        if (tableView != null) {
            oras = getOras(file.getName());
        } else {
            oras = business.getJudet();
        }

        ComandaHelper comandaHelper = parseProductCodeAndCantity(stringDoubleMap, true);

        createAndSaveComanda(comandaHelper.getProdusIntegerMap(), business, oras, tableView, file.getName(), true);

        if (comandaHelper.getErrorMessage().length() > 1) {
            throw new GeneralExceptions("Command a fost salvata dar:\n" + comandaHelper.getErrorMessage());
        }
    }


    private Map<String, Double> readFromPDFFile(String content, String criteriuDeCautare) {
        Map<String, Double> productCodeAndCantity = new HashMap<>();

        boolean startComparing = false;
        int contorCurent = 1;

        String[] lines = content.split(System.getProperty("line.separator"));

        String productCode = EMPTY_SPACE;
        String cantity;

        for (String line : lines) {
            if (line.contains(criteriuDeCautare)) {
                startComparing = true;
            }

            if (startComparing) {
                if (line.startsWith(String.valueOf(contorCurent) + WHITE_SPACE) && line.length() > 4) {
                    productCode = line.substring(line.indexOf(WHITE_SPACE) + 1);
                    productCode = productCode.substring(0, productCode.indexOf(WHITE_SPACE));
                    contorCurent++;
                }

                if (line.matches(LINE_CANTITY_SEPARATOR_REGEX) && !StringHelper.isEmpty(productCode) && line.length() > 4) {
                    cantity = line.substring(line.indexOf(WHITE_SPACE) + 1);
                    cantity = cantity.substring(0, cantity.indexOf(WHITE_SPACE));

                    double cantitateExistenta = productCodeAndCantity.get(productCode) != null ? productCodeAndCantity.get(productCode):0;
                    productCodeAndCantity.put(productCode, cantitateExistenta + Double.parseDouble(cantity));
                    productCode = EMPTY_SPACE;
                }
            }
        }

        return productCodeAndCantity;
    }

}
