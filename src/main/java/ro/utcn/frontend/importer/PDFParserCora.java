package ro.utcn.frontend.importer;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.backend.model.Business;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.helper.ComandaHelper;
import ro.utcn.helper.GeneralHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ro.utcn.Constants.CRITERIU_CAUTARE_FISIER_EXCEL;
import static ro.utcn.exceptions.GeneralExceptions.SETEAZA_UN_CRITERIU_DE_CAUTRARE_IN_SETARI;
import static ro.utcn.helper.GeneralHelper.WHITE_SPACE;

/**
 * Cora parser
 * <p>
 * Created by Lucian on 6/14/2017.
 */

@Component
public class PDFParserCora extends FileParser {

    @Autowired
    private GeneralHelper generalHelper;

    @Override
    public void parseFileAndSave(File file, Business business, TableView tableView, ListView listView) throws IOException, GeneralExceptions {
        Map<String, Double> productCodeAndCantity = new HashMap<>();

        PDDocument document = PDDocument.load(file);
        PDFTextStripper s = new PDFTextStripper();
        String content = s.getText(document);

        boolean startComparing = false;
        int contorCurent = 1;

        String setareGeneralaCriteriu = generalHelper.getSetareGeneralaFromSetari(CRITERIU_CAUTARE_FISIER_EXCEL, SETEAZA_UN_CRITERIU_DE_CAUTRARE_IN_SETARI);

        String[] lines = content.split(System.getProperty("line.separator"));
        String productCode;

        for (String line : lines) {
            if (line.contains(setareGeneralaCriteriu)) {
                startComparing = true;
            }

            if (startComparing) {
                if (line.startsWith(String.valueOf(contorCurent) + WHITE_SPACE) && line.length() > 4) {
                    productCode = line.substring(line.indexOf(WHITE_SPACE) + 1);
                    productCode = productCode.substring(0, productCode.indexOf(WHITE_SPACE));

                    try {
                        String[] linesSplitted = line.split(WHITE_SPACE);
                        productCodeAndCantity.put(productCode, Double.parseDouble(linesSplitted[linesSplitted.length - 1]));
                    } catch (Exception ignored) {
                    }

                    contorCurent++;
                }

            }
        }

        ComandaHelper comandaHelper = parseProductCodeAndCantity(productCodeAndCantity, false);

        createAndSaveComanda(comandaHelper.getProdusIntegerMap(), business, business.getJudet(), tableView, file.getName(), false);

        if (comandaHelper.getErrorMessage().length() > 1) {
            throw new GeneralExceptions("Command a fost salvata dar:\n" + comandaHelper.getErrorMessage());
        }
    }
}
