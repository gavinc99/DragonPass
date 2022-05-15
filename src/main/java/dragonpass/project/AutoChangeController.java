package dragonpass.project;

import Crypto.Cypher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Logger;
import model.PasswordManager;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;


public class AutoChangeController implements Initializable {

    @FXML
    private Button backBtn;

    @FXML
    private TableView<PasswordManager> tableManager;

    @FXML
    private TableColumn<PasswordManager, String> col_app;

    @FXML
    private TableColumn<PasswordManager, String> col_username;

    @FXML
    private TableColumn<PasswordManager, String> col_autoChange;

    @FXML
    private MenuButton menu;

    @FXML
    private Label accountLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label errorLabel;


    Subject monitor = new Subject();
    Observer observe1 = new SQLiteObserver(monitor);
    Observer observe2 = new FileObserver(monitor);

    Connection conn;
    ResultSet rs;

    String time = "";

    ObservableList<PasswordManager> PasswordList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        loadData();

        try {


        } catch (Exception e) {
            monitor.setLog(new Logger("warn", e.getMessage()));
        }

    }

    private void loadData() {

        col_app.setCellValueFactory(new PropertyValueFactory<>("app"));
        col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
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



    @FXML
    void accountSelectAction(MouseEvent event) {

            String account = tableManager.getSelectionModel().getSelectedItem().getApp();
            accountLabel.setText(account);

    }


    @FXML
    public String thirtySecondsAction(ActionEvent event) {

        time = "thirtySecs";

        timeLabel.setText("30 Seconds");

        return time;

    }



    @FXML
    public String fiveMinutesAction(ActionEvent event) {

        time = "fiveMins";

        timeLabel.setText("5 Minutes");

        return time;

    }

    @FXML
    public String fifteenMinutesAction(ActionEvent event) {

        time = "fifteenMins";

        timeLabel.setText("15 Minutes");

        return time;


    }

    @FXML
    public String thirtyMinutesAction(ActionEvent event) {

        time = "thirtyMins";

        timeLabel.setText("30 Minutes");

        return time;


    }

    @FXML
    public String oneHourAction(ActionEvent event) {

        time = "oneHour";

        timeLabel.setText("1 Hour");

        return time;


    }

    public void autoChangeAction(ActionEvent event) {

        try {
            if (!time.isBlank()) {

                try {

                    if (Objects.equals(time, "thirtySecs")) {


                        if (tableManager.getSelectionModel().getSelectedItem().getApp().isBlank()) {

                            errorLabel.setText("Please select an account for auto-change!");
                            monitor.setLog(new Logger("info", "Account for auto-change not selected"));

                        } else {

                            String username = tableManager.getSelectionModel().getSelectedItem().getApp();


                            String query = "UPDATE  " + UserSession.instance.getUserName() + " SET AutoChange = + " + "'" + "30 Seconds" + "'" + " WHERE app = " + "'" + username + "'";

                            DatabaseConnection connect = new DatabaseConnection();
                            conn = connect.getVerification();
                            PreparedStatement stmt = conn.prepareStatement(query);

                            stmt.executeUpdate();

                            accountLabel.setText("");
                            timeLabel.setText("");
                            errorLabel.setText("");

                            try {
                                PasswordList.clear();
                                String query2 = "SELECT * FROM " + UserSession.instance.getUserName(); //Retrieves username from user session


                                PreparedStatement stmt2 = conn.prepareStatement(query2);

                                rs = stmt2.executeQuery();

                                time = null;

                                while (rs.next()) {

                                    PasswordList.add(new PasswordManager(
                                            rs.getString("app"),
                                            rs.getString("username"),
                                            rs.getString("password"),
                                            rs.getString("AutoChange")
                                    ));
                                    tableManager.setItems(PasswordList);

                                }
                            } catch (Exception e) {
                                monitor.setLog(new Logger("warn", e.getMessage()));
                            }


                        }

                    } else if (Objects.equals(time, "fiveMins")) {

                        if (tableManager.getSelectionModel().getSelectedItem().getApp().isBlank()) {

                            errorLabel.setText("Please select an account for auto-change!");
                            monitor.setLog(new Logger("info", "Account for auto-change not selected"));

                        } else {

                            String username = tableManager.getSelectionModel().getSelectedItem().getApp();


                            String query = "UPDATE  " + UserSession.instance.getUserName() + " SET AutoChange = + " + "'" + "5 Minutes" + "'" + " WHERE app = " + "'" + username + "'";

                            DatabaseConnection connect = new DatabaseConnection();
                            conn = connect.getVerification();
                            PreparedStatement stmt = conn.prepareStatement(query);

                            stmt.executeUpdate();

                            accountLabel.setText("");
                            timeLabel.setText("");
                            errorLabel.setText("");

                            try {
                                PasswordList.clear();
                                String query2 = "SELECT * FROM " + UserSession.instance.getUserName(); //Retrieves username from user session


                                PreparedStatement stmt2 = conn.prepareStatement(query2);

                                rs = stmt2.executeQuery();

                                time = null;

                                while (rs.next()) {

                                    PasswordList.add(new PasswordManager(
                                            rs.getString("app"),
                                            rs.getString("username"),
                                            rs.getString("password"),
                                            rs.getString("AutoChange")
                                    ));
                                    tableManager.setItems(PasswordList);

                                }
                            } catch (Exception e) {
                                monitor.setLog(new Logger("warn", e.getMessage()));
                            }

                        }

                    } else if (Objects.equals(time, "fifteenMins")) {

                        if (tableManager.getSelectionModel().getSelectedItem().getApp().isBlank()) {

                            errorLabel.setText("Please select an account for auto-change!");
                            monitor.setLog(new Logger("info", "Account for auto-change not selected"));

                        } else {

                            String username = tableManager.getSelectionModel().getSelectedItem().getApp();


                            String query = "UPDATE  " + UserSession.instance.getUserName() + " SET AutoChange = + " + "'" + "15 Minutes" + "'" + " WHERE app = " + "'" + username + "'";

                            DatabaseConnection connect = new DatabaseConnection();
                            conn = connect.getVerification();
                            PreparedStatement stmt = conn.prepareStatement(query);

                            stmt.executeUpdate();

                            accountLabel.setText("");
                            timeLabel.setText("");
                            errorLabel.setText("");

                            try {
                                PasswordList.clear();
                                String query2 = "SELECT * FROM " + UserSession.instance.getUserName(); //Retrieves username from user session


                                PreparedStatement stmt2 = conn.prepareStatement(query2);

                                rs = stmt2.executeQuery();

                                time = null;

                                while (rs.next()) {

                                    PasswordList.add(new PasswordManager(
                                            rs.getString("app"),
                                            rs.getString("username"),
                                            rs.getString("password"),
                                            rs.getString("AutoChange")
                                    ));
                                    tableManager.setItems(PasswordList);

                                }
                            } catch (Exception e) {
                                monitor.setLog(new Logger("warn", e.getMessage()));
                            }

                        }
                    } else if (Objects.equals(time, "thirtyMins")) {

                        if (tableManager.getSelectionModel().getSelectedItem().getApp().isBlank()) {

                            errorLabel.setText("Please select an account for auto-change!");
                            monitor.setLog(new Logger("info", "Account for auto-change not selected"));

                        } else {

                            String username = tableManager.getSelectionModel().getSelectedItem().getApp();


                            String query = "UPDATE  " + UserSession.instance.getUserName() + " SET AutoChange = + " + "'" + "30 Minutes" + "'" + " WHERE app = " + "'" + username + "'";

                            DatabaseConnection connect = new DatabaseConnection();
                            conn = connect.getVerification();
                            PreparedStatement stmt = conn.prepareStatement(query);

                            stmt.executeUpdate();

                            accountLabel.setText("");
                            timeLabel.setText("");
                            errorLabel.setText("");

                            try {
                                PasswordList.clear();
                                String query2 = "SELECT * FROM " + UserSession.instance.getUserName(); //Retrieves username from user session


                                PreparedStatement stmt2 = conn.prepareStatement(query2);

                                rs = stmt2.executeQuery();

                                time = null;

                                while (rs.next()) {

                                    PasswordList.add(new PasswordManager(
                                            rs.getString("app"),
                                            rs.getString("username"),
                                            rs.getString("password"),
                                            rs.getString("AutoChange")
                                    ));
                                    tableManager.setItems(PasswordList);

                                }
                            } catch (Exception e) {
                                monitor.setLog(new Logger("warn", e.getMessage()));
                            }

                        }
                    } else if (Objects.equals(time, "oneHour")) {

                        if (tableManager.getSelectionModel().getSelectedItem().getApp().isBlank()) {

                            errorLabel.setText("Please select an account for auto-change!");
                            monitor.setLog(new Logger("info", "Account for auto-change not selected"));

                        } else {

                            String username = tableManager.getSelectionModel().getSelectedItem().getApp();


                            String query = "UPDATE  " + UserSession.instance.getUserName() + " SET AutoChange = + " + "'" + "1 Hour" + "'" + " WHERE app = " + "'" + username + "'";

                            DatabaseConnection connect = new DatabaseConnection();
                            conn = connect.getVerification();
                            PreparedStatement stmt = conn.prepareStatement(query);

                            stmt.executeUpdate();

                            accountLabel.setText("");
                            timeLabel.setText("");
                            errorLabel.setText("");

                            try {
                                PasswordList.clear();
                                String query2 = "SELECT * FROM " + UserSession.instance.getUserName(); //Retrieves username from user session


                                PreparedStatement stmt2 = conn.prepareStatement(query2);

                                rs = stmt2.executeQuery();

                                time = null;

                                while (rs.next()) {

                                    PasswordList.add(new PasswordManager(
                                            rs.getString("app"),
                                            rs.getString("username"),
                                            rs.getString("password"),
                                            rs.getString("AutoChange")
                                    ));
                                    tableManager.setItems(PasswordList);

                                }
                            } catch (Exception e) {
                                monitor.setLog(new Logger("warn", e.getMessage()));
                            }

                        }

                    }
                } catch (Exception e) {

                    errorLabel.setText("Please select an account for auto-change!");
                    monitor.setLog(new Logger("info", "Account for auto-change not selected"));
                    monitor.setLog(new Logger("warn", e.getMessage()));

                }
            } else {
                monitor.setLog(new Logger("info", "Time interval not selected"));
                errorLabel.setText("Time interval not selected!");
            }

        }
        catch(Exception e){

            monitor.setLog(new Logger("info", "Time interval not selected"));
            errorLabel.setText("Pleas enter an account and time interval!");

        }
    }

    @FXML
    void removeBtnAction(ActionEvent event) throws SQLException {

        try {
            String username = tableManager.getSelectionModel().getSelectedItem().getApp();


            String query = "UPDATE  " + UserSession.instance.getUserName() + " SET AutoChange = + " + "'" + "Inactive" + "'" + " WHERE app = " + "'" + username + "'";

            DatabaseConnection connect = new DatabaseConnection();
            conn = connect.getVerification();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.executeUpdate();

            try {
                PasswordList.clear();
                String query2 = "SELECT * FROM " + UserSession.instance.getUserName(); //Retrieves username from user session


                PreparedStatement stmt2 = conn.prepareStatement(query2);

                rs = stmt2.executeQuery();

                time = null;
                accountLabel.setText("");
                timeLabel.setText("");
                errorLabel.setText("");

                while (rs.next()) {

                    PasswordList.add(new PasswordManager(
                            rs.getString("app"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("AutoChange")
                    ));
                    tableManager.setItems(PasswordList);

                }
            } catch (Exception e) {
                monitor.setLog(new Logger("warn", e.getMessage()));
            }
        }
        catch(Exception e){
            monitor.setLog(new Logger("warn", e.getMessage()));
        }

    }


    //Exit button action
    public void backBtnAction() throws IOException {

        //Close stage
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.close();

        //Loads FXML for menu
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Dragon Pass");
        stage.setScene(scene);
        stage.show();
        monitor.setLog(new Logger("info", "MenuController Accessed"));

    }

}
