package ro.utcn.frontend.controller.comenzi;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.frontend.controller.GeneralActions;
import ro.utcn.frontend.frontendServices.InitializeService;
import ro.utcn.backend.backendservices.BusinessService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * Created by Lucian on 4/7/2017.
 */

@Component
public class ComenziController extends GeneralActions implements Initializable {

    @FXML
    public AnchorPane comenziAnchorPane;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private InitializeService initializeService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeService.loadInitialComenziPage(businessService.getBusinessAvailableInCommands(), comenziAnchorPane);
    }
}

