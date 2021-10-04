package ro.utcn.frontend.controller;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Manager;

import java.io.IOException;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * Ge
 * Created by Lucian on 4/7/2017.
 */

@Component
public class GeneralActions {

    private Logger LOGGER = LogManager.getLogger(GeneralActions.class);

    @Autowired
    private Manager manager;

    public void homeAction() {
        try {
            manager.loadMainPage();
        } catch (IOException e) {
            LOGGER.error(e);
            alertWithError(e.getMessage());
        }
    }

    public void exitAction() {
        System.exit(1);
    }

    public void comenziAction() {
        try {
            manager.loadComenziPage();
        } catch (IOException e) {
            LOGGER.error(e);
            alertWithError(e.getMessage());
        }
    }

    public void zileLibere() {
        try {
            manager.loadHolidayPage();
        } catch (IOException e) {
            alertWithError(e.getMessage());
        }
    }

    public void istoricAction() {
        try {
            manager.loadComandaIstoricPage(manager.getBusinessFromComandaTabel().getNume());
        } catch (IOException e) {
            LOGGER.error("Failed to load comanda istoric page", e);
            alertWithError("Eroare" + e.getMessage());
        }
    }

    public void backAngajati() {
        try {
            manager.loadAngajatiPage();
        } catch (IOException e) {
            LOGGER.error(e);
            alertWithError(e.getMessage());
        }
    }

    public void navigareLaPontaj() {
        try {
            manager.loadPontajPage();
        } catch (IOException e) {
            LOGGER.error(e);
            alertWithError(e.getMessage());
        }
    }

}
