package ro.utcn;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Employee;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by Lucian on 3/31/2017.
 */

@Component
public class Manager {

    @Resource(name = "windowWidth")
    private int windowWidth;

    @Resource(name = "windowHeight")
    private int windowHeight;

    private Stage stage;
    private Business businessFromComandaTabel;
    private Employee employee;

    public void loadMainPage() throws IOException {
        loadAndSetPage("Chimdet 2.0", Constants.MAIN_PAGE, windowWidth, windowHeight);
    }

    public void loadComenziPage() throws IOException {
        loadAndSetPage("Comenzi", Constants.COMENZI_PAGE, windowWidth, windowHeight);
    }

    public void loadComandaTabelPage() throws IOException {
        loadAndSetPage("Tabel", Constants.COMAND_TABEL_PAGE, windowWidth, windowHeight);
    }

    public void loadComandaListaPage() throws IOException {
        loadAndSetPage("Comanda Lista", Constants.COMAND_LISTA_PAGE, windowWidth, windowHeight);
    }

    public void loadFirmePage() throws IOException {
        loadAndSetPage("Firme", Constants.FIRME_PAGE, windowWidth, windowHeight);
    }

    public void loadProdusePage() throws IOException {
        loadAndSetPage("Produse", Constants.PRODUSE_PAGE, windowWidth, windowHeight);
    }

    public void loadSetariPage() throws IOException {
        loadAndSetPage("Setari", Constants.SETARI_PAGE, windowWidth, windowHeight);
    }

    public void loadRapoartePage() throws IOException {
        loadAndSetPage("Rapoarte", Constants.RAPOARTE_PAGE, windowWidth, windowHeight);
    }

    public void loadUserPage() throws IOException {
        loadAndSetPage("Utilizator", Constants.USER_PAGE, windowWidth / 2, windowHeight / 2);
    }

    public void loadComandaIstoricPage(String numeFirma) throws IOException {
        loadAndSetPage("Istoric - firma:" + numeFirma, Constants.COMAND_ISTORIC_PAGE, windowWidth, windowHeight);
    }

    public void loadAngajatiPage() throws IOException {
        loadAndSetPage("Angajati", Constants.ANGAJATI_PAGE, windowWidth, windowHeight);
    }

    public void loadPreturiPage() throws IOException {
        loadAndSetPage("Preturi", Constants.PRETURI_PAGE, windowWidth, windowHeight);
    }

    public void loadPontajPage() throws IOException {
        loadAndSetPage("Pontaj", Constants.PONTAJ_PAGE, windowWidth, windowHeight);
    }

    public void loadHolidayPage() throws IOException {
        loadAndSetPage("Zile libere", Constants.HOLIDAY_PAGE, windowWidth, windowHeight);
    }


    public void loadConcediuPage(String title) throws IOException {
        loadAndSetPage(title, Constants.CONCEDIU_PAGE, windowWidth, windowHeight);
    }

    private FXMLLoader loadFXML(String path) {
        FXMLLoader pagesLoader = new FXMLLoader(getClass().getResource(path));
        pagesLoader.setControllerFactory(Main.ctx::getBean);
        return pagesLoader;
    }

    private void loadAndSetPage(String title, String pageName, int windowWidth, int windowHeight) throws IOException {
        stage.close();
        stage.getIcons().add(new Image("images/icon.png"));
        FXMLLoader pagesLoader = loadFXML(pageName);
        stage.setTitle(title);
        loadPage(stage, pagesLoader, windowWidth, windowHeight);
    }

    /**
     * Used to load the given page
     */
    private void loadPage(Stage stage, FXMLLoader pagesLoader, int windowWidth, int windowHeight) throws IOException {
        Parent root = pagesLoader.load();
        Scene scene = new Scene(root, windowWidth, windowHeight);
        stage.setScene(scene);
        stage.setMinHeight(windowHeight);
        stage.setMinWidth(windowWidth);
        stage.setMaxHeight(windowHeight);
        stage.setMaxWidth(windowWidth);
        stage.centerOnScreen();
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Business getBusinessFromComandaTabel() {
        return businessFromComandaTabel;
    }

    public void setBusinessFromComandaTabel(Business businessFromComandaTabel) {
        this.businessFromComandaTabel = businessFromComandaTabel;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
