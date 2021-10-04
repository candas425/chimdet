package ro.utcn.frontend.controller.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Manager;
import ro.utcn.backend.backendservices.UserService;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.User;
import ro.utcn.frontend.controller.GeneralActions;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static ro.utcn.frontend.frontendServices.PopUpService.alertWithError;

/**
 * User
 * <p>
 * Created by Lucian on 5/11/2017.
 */

@Component
public class UserController extends GeneralActions implements Initializable {

    @FXML
    public AnchorPane userAnchorPane;

    @Autowired
    private UserService userService;
    @Autowired
    private Manager manager;

    @Resource(name = "userUsername")
    private String userUsername;

    @Resource(name = "userPassword")
    private String userPassword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BorderPane bp = new BorderPane();
        bp.setLayoutY(24);
        bp.setPadding(new Insets(50, 120, 80, 100));

        //Adding HBox
        HBox hb = new HBox();
        hb.setPadding(new Insets(20, 20, 20, 30));

        //Adding GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        //Implementing Nodes for GridPane
        Label lblUserName = new Label("Username");
        final TextField txtUserName = new TextField();
        Label lblPassword = new Label("Parola");
        final PasswordField pf = new PasswordField();
        Button btnLogin = new Button("Intra");
        final Label lblMessage = new Label();

        //Adding Nodes to GridPane layout
        gridPane.add(lblUserName, 0, 0);
        gridPane.add(txtUserName, 1, 0);
        gridPane.add(lblPassword, 0, 1);
        gridPane.add(pf, 1, 1);
        gridPane.add(btnLogin, 2, 1);
        gridPane.add(lblMessage, 1, 2);

        //Reflection for gridPane
        Reflection r = new Reflection();
        r.setFraction(0.7f);
        gridPane.setEffect(r);

        //DropShadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);

        //Adding text and DropShadow effect to it
        Text text = new Text("Autentificare");
        text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        text.setEffect(dropShadow);

        //Adding text to HBox
        hb.getChildren().add(text);

        //Add ID's to Nodes
        bp.setId("bp");
        gridPane.setId("root");
        btnLogin.setId("btnLogin");
        text.setId("text");

        btnLogin.setOnAction(event -> {
            try {
                userScript(txtUserName.getText(), pf.getText());
            } catch (PersistanceException | IOException e) {
                alertWithError(e.getMessage());
            }
        });

        //Add HBox and GridPane layout to BorderPane Layout
        bp.setTop(hb);
        bp.setCenter(gridPane);

        userAnchorPane.getChildren().add(bp);
    }

    private void userScript(String username, String password) throws PersistanceException, IOException {
        List<User> userList = userService.getAll();
        if (userList.isEmpty()) {
            User user = new User();
            user.setUsername(userUsername);
            user.setParola(userPassword);
            userService.saveUser(user);
        } else {
            User user = userList.get(0);
            user.setUsername(userUsername);
            user.setParola(userPassword);
            userService.updateUser(user);
        }

        User user = userService.getUserWithUsernameAndPassword(username, password);
        if (user == null) {
            alertWithError("Username sau parola gresite");
        } else {
            manager.loadSetariPage();
        }
    }
}
