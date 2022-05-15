package dragonpass.project;

import Crypto.Cypher;
import Crypto.Hash;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Logger;
import monitor.FileObserver;
import monitor.Observer;
import monitor.SQLiteObserver;
import monitor.Subject;
import model.UserSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class ManagerAddController{

    Subject monitor = new Subject();
    Observer observe1 = new SQLiteObserver(monitor);
    Observer observe2 = new FileObserver(monitor);

    @FXML
    private TextField appNameTF;

    @FXML
    private Button exitB;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private Button submitBtn;

    @FXML
    private TextField usernameTF;

    @FXML
    private Label invalidApp;

    @FXML
    private Label invalidPassword;

    @FXML
    private Label invalidUsername;



    public void exitBAction(javafx.event.ActionEvent event) throws IOException {

        //Close stage
        Stage stage = (Stage) exitB.getScene().getWindow();
        stage.close();

        //Loads FXML for menu
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("passwordManager.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 749, 500);
        stage.setTitle("Dragon Pass");
        stage.setScene(scene);
        stage.show();
        monitor.setLog(new Logger("info", "Password Manager Accessed"));

    }

    public void submitBtnAction(javafx.event.ActionEvent event) throws Exception {

        Boolean validApp = null;
        Boolean validUsername = null;
        Boolean validPassword = null;

        //App/Site name field blank
        if(appNameTF.getText().isBlank()){

            invalidApp.setText("Please enter an app name!");

        }
        else if(appNameTF.getText().length() > 30){

            invalidApp.setText("App name must be less than 30 characters!");

        }
        else{

            invalidApp.setText("✔");
            validApp = true;
        }

        //Username field blank
        if(usernameTF.getText().isBlank()){

            invalidUsername.setText("Please enter a username!");

        }

        else if(usernameTF.getText().length() > 30){

            invalidUsername.setText("Username must be less than 30 characters!");

        }
        else{

            invalidUsername.setText("✔");
            validUsername = true;
        }

        //Password field blank
        if(passwordTF.getText().isBlank()){

            invalidPassword.setText("Please enter a password!");

        }
        else if(passwordTF.getText().length() > 30){

            invalidPassword.setText("Password must be less than 30 characters!");

        }
        else{

            invalidPassword.setText("✔");
            validPassword = true;

        }

        if(Boolean.TRUE.equals(validApp)
                && Boolean.TRUE.equals(validUsername)
                && Boolean.TRUE.equals(validPassword)){

            submit();

        }

        else{

            monitor.setLog(new Logger("info", "Submission details invalid"));

        }


    }


    private void submit() throws Exception {

        String PASSWORD = UserSession.instance.getPasswordHash();


        String app = appNameTF.getText();
        String username = usernameTF.getText();
        String hashPass = Hash.toHexString(Hash.getSHA(passwordTF.getText()));

        //calls encryption class to encrypt user password
        String password = Cypher.encrypt(passwordTF.getText().getBytes(StandardCharsets.UTF_8), PASSWORD);

        Connection conn;

        String query = "INSERT INTO " + UserSession.instance.getUserName() + " (app, username, password, passHashed) VALUES (?,?,?,?);";

        DatabaseConnection connect = new DatabaseConnection();
        conn = connect.getVerification();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, app);
        stmt.setString(2, username);
        stmt.setString(3, password);
        stmt.setString(4, hashPass);

        try{

            stmt.execute();
            monitor.setLog(new Logger("info", "New column added"));

            Stage stage = (Stage) exitB.getScene().getWindow();
            stage.close();


            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("passwordManager.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 828, 437);
            stage.setTitle("Dragon Pass");
            stage.setScene(scene);
            stage.show();

        }
        catch(Exception e){

            monitor.setLog(new Logger("warn", e.getMessage()));

        }

    }


}
