package dragonpass.project;

import Crypto.Cypher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Logger;
import model.AutoChange;
import monitor.FileObserver;
import monitor.Observer;
import monitor.SQLiteObserver;
import monitor.Subject;

import model.UserSession;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    Subject monitor = new Subject();
    Observer observe1 = new SQLiteObserver(monitor);
    Observer observe2 = new FileObserver(monitor);

    @FXML
    private Button exitB;
    @FXML
    private Button LOBtn;
    @FXML
    private Button pManBtn;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }



    //Button for logging out
    public void LOBtnAction(ActionEvent event) throws IOException, SQLException {

        UserSession.cleanUserSession(); //User session closed
        AutoChange.cleanAutoChange(); //Stops auto change


        monitor.setLog(new Logger("info", "Auto-Change stopped"));
        monitor.setLog(new Logger("info", "User Session Closed"));

        Stage stage = (Stage) LOBtn.getScene().getWindow();
        stage.close();


        //Loads FXML for login
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Dragon Pass");
        stage.setScene(scene);
        stage.show();
        monitor.setLog(new Logger("info", "LogOut successful"));

    }

    //Button for password manager
    public void pManBtnAction (ActionEvent event) throws IOException {

        //Close stage
        Stage stage = (Stage) pManBtn.getScene().getWindow();
        stage.close();

        //Loads FXML for password manager
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("passwordManager.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 828, 437);
        stage.setTitle("Dragon Pass");
        stage.setScene(scene);
        stage.show();
        monitor.setLog(new Logger("info", "Password Manager Accessed"));

    }

    //Button for auto-change feature
    public void autoChangeBtnAction(ActionEvent event) throws IOException {

        //Close stage
        Stage stage = (Stage) pManBtn.getScene().getWindow();
        stage.close();

        //Loads FXML for password manager
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("AutoChange.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 826, 474);
        stage.setTitle("Dragon Pass");
        stage.setScene(scene);
        stage.show();
        monitor.setLog(new Logger("info", "Auto-Change Accessed"));

    }

    public void lockBtnAction(ActionEvent event) throws IOException {

        //Close stage
        Stage stage = (Stage) pManBtn.getScene().getWindow();
        stage.close();

        //Loads FXML for lock controller
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("lock.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 398, 351);
        stage.setTitle("Dragon Pass");
        stage.setScene(scene);
        stage.show();
        monitor.setLog(new Logger("info", "Lock Enabled"));

    }

}
