package dragonpass.project;

import Crypto.Cypher;
import javafx.scene.control.*;
import model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Logger;
import model.PasswordManager;
import monitor.FileObserver;
import monitor.Observer;
import monitor.SQLiteObserver;
import monitor.Subject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManagerController implements Initializable {

    @FXML
    private Button backB;

    @FXML
    private TableView<PasswordManager> tableManager;

    @FXML
    private TableColumn<PasswordManager, String> col_app;

    @FXML
    private TableColumn<PasswordManager, String> col_username;

    @FXML
    private TableColumn<PasswordManager, String> col_password;

    @FXML
    private TableColumn<PasswordManager, String> col_autoChange;

    @FXML
    private Button addTable;

    @FXML
    private TextField revealText;





    Subject monitor = new Subject();
    Observer observe1 = new SQLiteObserver(monitor);
    Observer observe2 = new FileObserver(monitor);


    Connection conn;
    ResultSet rs;


    ObservableList<PasswordManager> PasswordList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        loadData();

    }


    //loads data for password manager table
    private void loadData() {

        col_app.setCellValueFactory(new PropertyValueFactory<>("app"));
        col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        col_password.setCellValueFactory(new PropertyValueFactory<>("password"));
        col_autoChange.setCellValueFactory(new PropertyValueFactory<>("AutoChange"));

        try {
            PasswordList.clear();
            String query = "SELECT * FROM " + UserSession.instance.getUserName(); //Retrieves username from user session

            DatabaseConnection connect = new DatabaseConnection();
            conn = connect.getVerification();
            PreparedStatement stmt = conn.prepareStatement(query);

            rs = stmt.executeQuery();


            while (rs.next()) {

                PasswordList.add(new PasswordManager(
                        rs.getString("app"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("AutoChange")
                ));
                tableManager.setItems(PasswordList);

            }
        }
        catch(Exception e){
            monitor.setLog(new Logger("warn", e.getMessage()));
        }

    }


    public void addTableAction() throws IOException {

        //Close stage
        Stage stage = (Stage) addTable.getScene().getWindow();
        stage.close();

        //Loads FXML for add column manager
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("addManager.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 482, 441);
        stage.setTitle("Dragon Pass");
        stage.setScene(scene);
        stage.show();
        monitor.setLog(new Logger("info", "MenuController Accessed"));

    }

    


    //Exit button action
    public void backBAction() throws IOException {

        //Close stage
        Stage stage = (Stage) backB.getScene().getWindow();
        stage.close();

        //Loads FXML for menu
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Dragon Pass");
        stage.setScene(scene);
        stage.show();
        monitor.setLog(new Logger("info", "MenuController Accessed"));

    }


    public void refreshTableAction(ActionEvent event){

        try {
            PasswordList.clear();
            String query = "SELECT * FROM " + UserSession.instance.getUserName(); //Retrieves username from user session

            DatabaseConnection connect = new DatabaseConnection();
            conn = connect.getVerification();
            PreparedStatement stmt = conn.prepareStatement(query);

            rs = stmt.executeQuery();


            while (rs.next()) {

                PasswordList.add(new PasswordManager(
                        rs.getString("app"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("AutoChange")
                ));
                tableManager.setItems(PasswordList);

            }
        }
        catch(Exception e){
            monitor.setLog(new Logger("warn", e.getMessage()));
        }

    }

    public void deleteTableAction(ActionEvent event) throws SQLException {

        try {

            String query = "DELETE FROM " + UserSession.instance.getUserName() + " WHERE app = " + "'" + tableManager.getSelectionModel().getSelectedItem().getApp() + "'";

            DatabaseConnection connect = new DatabaseConnection();
            conn = connect.getVerification();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.executeUpdate();

            try {
                PasswordList.clear();
                String query2 = "SELECT * FROM " + UserSession.instance.getUserName();


                rs = stmt.executeQuery(query2);


                while (rs.next()) {
                    PasswordList.add(new PasswordManager(
                            rs.getString("app"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("AutoChange")));
                    tableManager.setItems(PasswordList);

                }
            }
            catch(Exception e){
                monitor.setLog(new Logger("warn", e.getMessage()));
            }


        }
        catch(Exception e){
            monitor.setLog(new Logger("warn", e.getMessage()));
        }

    }

    public void revealTableAction(ActionEvent event) throws Exception {


        try {
            String PASSWORD = UserSession.instance.getPasswordHash();
            String pass = tableManager.getSelectionModel().getSelectedItem().getPassword();

            String decryptedPass = Cypher.decrypt(pass, PASSWORD);

            revealText.setText(decryptedPass); //Reveals decrypted password to verified user

        }
        catch(Exception e){
            monitor.setLog(new Logger("warn", e.getMessage()));
        }


    }



}
