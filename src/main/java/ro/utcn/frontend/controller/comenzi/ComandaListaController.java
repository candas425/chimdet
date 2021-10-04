package ro.utcn.frontend.controller.comenzi;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Manager;
import ro.utcn.backend.backendservices.CommandService;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Command;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Product;
import ro.utcn.backend.model.enums.TipFisier;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.frontend.frontendServices.InitializeService;
import ro.utcn.frontend.frontendServices.helper.ListRowComanda;
import ro.utcn.frontend.importer.PDFParserCora;
import ro.utcn.helper.GeneralHelper;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithDetails;
import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;
import static ro.utcn.helper.JavaFxComponentsHelper.createLabel;

/**
 * Created by Lucian on 5/4/2017.
 */

@Component
public class ComandaListaController extends GeneralActions implements Initializable {

    @FXML
    public Label textCautat;

    private Logger LOGGER = LogManager.getLogger(ComandaListaController.class);

    @FXML
    public AnchorPane comandaListaAnchorPane;

    @Autowired
    private InitializeService initializeService;
    @Autowired
    private Manager manager;
    @Autowired
    private CommandService commandService;
    @Autowired
    private PDFParserCora pdfParserCora;

    private ListView<ListRowComanda> secondListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ListView<Product> listView = new ListView<>();
        secondListView = new ListView<>();
        initializeService.loadInitialComandaListaPage(comandaListaAnchorPane, listView, secondListView, textCautat);
        Business business = manager.getBusinessFromComandaTabel();
        comandaListaAnchorPane.getChildren().add(createLabel(15, 30, 26, "Client:" + business));

        secondListView.setOnDragDropped(this::mouseDragDropped);
        secondListView.setOnDragOver(GeneralHelper::mouseDragEnter);
    }


    private void mouseDragDropped(final DragEvent e) {
        Dragboard db = e.getDragboard();
        if (db.hasFiles()) {
            if (db.getFiles().size() > 1) {
                alertWithError("Doar cate un fisier");
                return;
            }

            // Only get the first file from the list
            File file = db.getFiles().get(0);
            String extension = GeneralHelper.getExtension(file.getName());
            try {
                if (extension.toUpperCase().equals(TipFisier.PDF.toString())) {
                    pdfParserCora.parseFileAndSave(file,manager.getBusinessFromComandaTabel(),null, null);
                    alertWithDetails("Command a fost salvata");
                }
            } catch (Exception e2) {
                alertWithError(e2.getMessage());
            }

        }
        e.consume();
    }

    public void adaugaComanda() {
        List<ListRowComanda> listRowComandaList = secondListView.getItems();
        if (listRowComandaList.size() > 0) {
            Command command = new Command();
            Business business = manager.getBusinessFromComandaTabel();
            command.setBusiness(manager.getBusinessFromComandaTabel());
            command.setData(LocalDateTime.now());
            command.setOras(business.getJudet());
            command.setAvailableInTableComanda(true);

            Map<Integer, Integer> produsCantitateMap = new HashMap<>();

            for (ListRowComanda listRowComanda : listRowComandaList) {
                produsCantitateMap.put(listRowComanda.getProduct().getId(), (int) listRowComanda.getCantitate());
            }
            command.setListaProduse(produsCantitateMap);

            try {
                commandService.saveCommand(command);
                alertWithDetails("Command a fost salvata");
                secondListView.setItems(FXCollections.observableArrayList(new ArrayList<>()));
            } catch (PersistanceException e) {
                LOGGER.error(e);
                alertWithError("Command nu a putut fi salvata, motivul:" + e.getMessage());
            }
        } else {
            alertWithError("Nu a fost adaugat niciun produs");
        }
    }

    public void golesteComanda() {
        secondListView.setItems(FXCollections.observableArrayList(new ArrayList<>()));
    }

    public void stergeProdusul() {
        ListRowComanda listRowComanda = secondListView.getSelectionModel().getSelectedItem();
        if (listRowComanda != null) {
            secondListView.getItems().remove(listRowComanda);
        } else {
            alertWithError("Alege un produs din tabelul din dreapta");
        }
    }
}
