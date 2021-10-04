package ro.utcn.frontend.importer;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.backend.backendservices.CommandService;
import ro.utcn.backend.backendservices.ProductService;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Command;
import ro.utcn.backend.model.Product;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.TableService;
import ro.utcn.frontend.frontendServices.helper.ComandaHelper;
import ro.utcn.helper.GeneralHelper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static ro.utcn.exceptions.GeneralExceptions.NUMELE_FISIERULUI_NU_ESTE_IN_LISTA_DE_ORASE_DIN_SETARI;

/**
 * Global template for file readers
 * Created by Lucian on 5/30/2017.
 */

@Component
public abstract class FileParser {

    private Logger LOGGER = LogManager.getLogger(FileParser.class);

    public static final String LINE_CANTITY_SEPARATOR_REGEX = "[ .0-9]+";

    @Autowired
    protected ProductService productService;
    @Autowired
    protected CommandService commandService;
    @Autowired
    protected GeneralHelper generalHelper;
    @Autowired
    protected TableService tableService;

    public abstract void parseFileAndSave(File file, Business business, TableView tableView, ListView listView) throws IOException, GeneralExceptions;

    protected String getOras(String fileName) throws GeneralExceptions, IOException {
        Map<String, String> mapOraseDinFisier = generalHelper.getOraseFromSetari();
        String value = null;
        for (String initiale : mapOraseDinFisier.values()) {
            String fileInitials = fileName.split("[ 0-9]")[0];
            if (fileInitials.toUpperCase().equals(initiale.toUpperCase())) {
                value = initiale;
            }
        }

        if (value != null) {
            for (Map.Entry<String, String> stringStringEntry : mapOraseDinFisier.entrySet()) {
                if (stringStringEntry.getValue().toUpperCase().equals(value.toUpperCase())) {
                    return stringStringEntry.getKey();
                }
            }
        }

        throw new GeneralExceptions(NUMELE_FISIERULUI_NU_ESTE_IN_LISTA_DE_ORASE_DIN_SETARI);
    }

    protected ComandaHelper parseProductCodeAndCantity(Map<String, Double> stringDoubleMap, boolean fromTabel) {
        ComandaHelper comandaHelper = new ComandaHelper();

        String errorMessage = "";
        Map<Integer, Integer> produsIntegerMap = new HashMap<>();
        if (stringDoubleMap.isEmpty()) {
            errorMessage += "Maparea din setari nu este buna pentru ca nu s-a gasit niciun produs";
        } else {
            for (Map.Entry<String, Double> stringDoubleEntry : stringDoubleMap.entrySet()) {
                Product product = null;
                if (fromTabel) {
                    product = productService.getProductByIdCodeAndAvailableInTable(stringDoubleEntry.getKey());
                } else {
                    product = productService.getProductByIdentifierCode(stringDoubleEntry.getKey());
                }
                if (product == null) {
                    errorMessage += "Codul " + stringDoubleEntry.getKey() + " nu este mapat la niciun product\n";
                } else {
                    produsIntegerMap.put(product.getId(), stringDoubleEntry.getValue().intValue());
                }
            }
        }

        comandaHelper.setErrorMessage(errorMessage);
        comandaHelper.setProdusIntegerMap(produsIntegerMap);
        return comandaHelper;
    }

    protected void createAndSaveComanda(Map<Integer, Integer> produsIntegerMap, Business business, String oras, TableView tableView, String fileName, boolean fromTabel) throws GeneralExceptions {
        Command command = new Command();
        command.setData(LocalDateTime.now());
        command.setListaProduse(produsIntegerMap);
        command.setBusiness(business);
        command.setOras(oras);
        command.setNumeFisier(fileName);
        command.setAvailableInTableComanda(true);
        try {
            commandService.saveCommand(command);
            if (fromTabel) {
                tableService.refreshComandaTabelWithData(tableView, business);
            }
        } catch (PersistanceException e) {
            LOGGER.error("Eroare la salvarea noii comenzi", e);
            throw new GeneralExceptions(e.getMessage());
        } catch (Exception e) {
            throw new GeneralExceptions(e.getMessage());
        }
    }

}
