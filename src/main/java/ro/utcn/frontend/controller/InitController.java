package ro.utcn.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Manager;
import ro.utcn.frontend.frontendServices.InitializeService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * First page controller
 *
 * Created by Lucian on 3/31/2017.
 */


@Component
public class InitController implements Initializable{

    private Logger LOGGER = LogManager.getLogger(InitController.class);

    @FXML
    private ImageView imageLogo;

    @FXML
    private ImageView versionLogo;

    @Autowired
    private Manager manager;

    @Autowired
    private InitializeService initializeService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeService.loadLogo(imageLogo);
        initializeService.loadVersionLogo(versionLogo);
    }

    //Comenzi
    public void meniuComenzi(){
        try {
            manager.loadComenziPage();
        } catch (IOException e) {
            LOGGER.error("Failed to load comenzi page",e);
            alertWithError(e.getMessage());
        }
    }

    //Firme
    public void meniuFirme(){
        try {
            manager.loadFirmePage();
        } catch (IOException e) {
            LOGGER.error("Failed to load firma page",e);
            alertWithError(e.getMessage());
        }
    }

    //Produse
    public void meniuProduse(){
        try{
            manager.loadProdusePage();
        } catch (IOException e) {
            LOGGER.error("Failed to load produse page",e);
            alertWithError(e.getMessage());
        }
    }

    //Setari
    public void meniuSetari(){
        try{
            manager.loadUserPage();
        } catch (IOException e) {
            LOGGER.error("Failed to load setari page",e);
            alertWithError(e.getMessage());
        }
    }

    //Rapoarte
    public void meniuRapoarte(){
        try{
            manager.loadRapoartePage();
        } catch (IOException e) {
            LOGGER.error("Failed to load rapoarte page",e);
            alertWithError(e.getMessage());
        }
    }

    //Angajati
    public void meniuAngajati(){
        try{
            manager.loadAngajatiPage();
        } catch (IOException e) {
            LOGGER.error("Failed to load angajati page",e);
            alertWithError(e.getMessage());
        }
    }
}
