package dragonpass.project;

import Crypto.Cypher;
import Crypto.Hash;
import Crypto.TwoFactorAuthentication;
import model.UserSession;
import model.AutoChange;
import model.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import monitor.FileObserver;
import monitor.Observer;
import monitor.SQLiteObserver;
import monitor.Subject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class LoginController {

    int counter = 0;

    Subject monitor = new Subject();
    Observer observe1 = new SQLiteObserver(monitor);
    Observer observe2 = new FileObserver(monitor);

    Connection conn = null;
    PreparedStatement stmt = null;

    @FXML
    private TextField usernameTF;

    @FXML
    private TextField passTF;

    @FXML
    private PasswordField pinTF;

    @FXML
    private TextField tfaTF;

    @FXML
    private Label invalidLabel;

    @FXML
    private Button exitB;


    //Login button action
    public void loginBtnAction(ActionEvent event) throws SQLException, NoSuchAlgorithmException, IOException {


        //If/else statements to prevent user from putting in wrong or empty details
        if (usernameTF.getText().isBlank() && passTF.getText().isBlank() && pinTF.getText().isBlank()) {

            invalidLabel.setText("Please enter your account details!");

        }

        //Username field blank
        else if (usernameTF.getText().isBlank()) {

            invalidLabel.setText("Please enter your username!");

        }

        //Password field blank
        else if (passTF.getText().isBlank()) {

            invalidLabel.setText("Please enter your password!");

        }

        //Pin field blank
        else if (pinTF.getText().isBlank()) {

            invalidLabel.setText("Please enter your pin!");

        }

        //2FA field blank
        else if (tfaTF.getText().isBlank()) {

            invalidLabel.setText("Please enter your TFA key!");

        } else {

            //If all text fields are filled, proceed to log in verification

            /* Note: Unlike registration, there is no need for further parameters in this process as the use of
            prepared statements will only accept credentials that match existing users from the database in the verification process */


            loginVerification();

        }
    }


    //Validates submission and prevents SQL Injection
    private void loginVerification() throws NoSuchAlgorithmException, SQLException, IOException {

        //Variables
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs;
        String secretKey = "";


        //setting user input to username
        String username = usernameTF.getText();

        //setting user input to password
        String password = "";
        password = Hash.toHexString(Hash.getSHA(passTF.getText()));

        String pass = "";
        pass = passTF.getText();
        //setting user input to pin
        String pin = "";
        pin = Hash.toHexString(Hash.getSHA(pinTF.getText()));

        //setting user input to 2FA key
        String key = "";
        key = tfaTF.getText();


        //Closes app if login details are incorrect 3 times
        if (counter == 2) {

            Stage stage = (Stage) exitB.getScene().getWindow();
            stage.close();
            monitor.setLog(new Logger("info", "Too many incorrect login attempts"));

        } else {

            try {

                monitor.setLog(new Logger("info", "Verification Requested"));

                //Creating a prepared statement to search and verify existence of user based on user input
                String query = "select * from user_account where username = ? and password = ? and pin = ?";

                //connecting to database
                DatabaseConnection connect = new DatabaseConnection();
                conn = connect.getVerification();
                stmt = conn.prepareStatement(query);

                //Parameters for query
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, pin);


                //Executing prepared statement
                rs = stmt.executeQuery();

                //Query was successful continue
                if (rs.next()) {

                    //Obtaining users unique secret key for two-factor authentication
                    secretKey = rs.getString("tfaKey");


                    int i = 0;
                    do {

                        //Setting users unique key to a String variable using the TOTP algorithm.
                        //This code will change every 30 seconds when the user tries to log in
                        String code = TwoFactorAuthentication.getTOTPCode(secretKey);

                    /* If the code from the user input matches the TOTP code variable then proceed. Based on the registration process,
                     the user should have the same code being generated on their phone via Google Authenticator */
                        if (Objects.equals(code, key)) {


                            monitor.setLog(new Logger("info", "Account verified"));

                            //Starts user session
                            UserSession.getInstance(username, pass);


                            monitor.setLog(new Logger("info", "User Session created"));

                            //Starts auto-change feature
                            String auto = "on";
                            AutoChange.getInstance(auto);

                            thirtySecsAC();
                            fiveMinsAC();
                            fifteenMinsAC();
                            thirtyMinsAC();
                            oneHourAC();


                            try {


                                //Close stage
                                Stage stage = (Stage) exitB.getScene().getWindow();
                                stage.close();

                                //Loads FXML for menu
                                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu.fxml"));
                                Scene scene = new Scene(fxmlLoader.load(), 800, 500);
                                stage.setTitle("Dragon Pass");
                                stage.setScene(scene);
                                stage.show();
                                monitor.setLog(new Logger("info", "MenuController accessed"));


                            } catch (Exception e) {

                                monitor.setLog(new Logger("warn", e.getMessage()));

                            }

                        }

                        //If 2FA details were incorrect
                        else {

                            counter = counter + 1;

                            int result = 3 - counter;


                            invalidLabel.setText("Incorrect. Login attempts remaining: " + result);

                            monitor.setLog(new Logger("info", "Invalid account details"));

                        }

                        i++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }


                    }
                    while (i < 1);

                }

                //If any details except 2FA were incorrect
                else {

                    counter = counter + 1;

                    int result = 3 - counter;


                    invalidLabel.setText("Incorrect. Login attempts remaining: " + result);

                    monitor.setLog(new Logger("info", "Invalid account details"));

                }

                rs.close();
            } catch (Exception e) {

                monitor.setLog(new Logger("warn", e.getMessage()));

            }

            //Close connection to database
            finally {
                try {

                    stmt.close();
                    conn.close();
                } catch (Exception e) {
                    monitor.setLog(new Logger("warn", e.getMessage()));
                }
            }
        }

    }


    //Register button action
    public void registerBtnAction(ActionEvent event) {

        Stage stage = (Stage) exitB.getScene().getWindow();
        stage.close();

        try {

            //Loads FXML for Registration tab
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("register.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 897, 651);
            stage.setTitle("Dragon Pass");
            stage.setScene(scene);
            stage.show();
            monitor.setLog(new Logger("info", "Register controller accessed"));

        } catch (Exception e) {

            monitor.setLog(new Logger("warn", e.getMessage()));

        }

    }

    //Auto-change feature for 30 second intervals
    private void thirtySecsAC() {


        try {
            Thread thread = new Thread(() -> {

                while (Objects.equals(AutoChange.instance.getAuto(), "on")) { //Only active when auto-change is set to 'on' from Model.AutoChange
                    String PASSWORD = UserSession.instance.getPasswordHash(); //calls user password by RAM memory for encryption
                    try {

                        int leftLimit = 48; // numeral '0'
                        int rightLimit = 122; // letter 'z'
                        int targetStringLength = 20; // length of generated string
                        Random random = new Random();

                        //Generates random string based on above parameters
                        String generatedString = random.ints(leftLimit, rightLimit + 1)
                                //.filter(i -> (i <= 1 || i >= 1) && (i <= 1 || i >= 1)) Option to filter generated string
                                .limit(targetStringLength)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();


                        //Encrypts generated string and sets it to a variable using the users account password as the encryption key
                        String password = Cypher.encrypt(generatedString.getBytes(StandardCharsets.UTF_8), PASSWORD);

                        //Hashes password for linked applications
                        String passHashed = Hash.toHexString(Hash.getSHA(generatedString));

                        //Updates users password in the database with the encrypted random string
                        String query1 = "UPDATE  " + UserSession.instance.getUserName() + " SET password = " + "'" + password + "'" + ", passHashed = " + "'" + passHashed + "'" + " WHERE AutoChange = " + "'" + "30 Seconds" + "'";

                        //Connects to database
                        DatabaseConnection connect1 = new DatabaseConnection();
                        conn = connect1.getVerification();
                        PreparedStatement stmt1 = conn.prepareStatement(query1);

                        //Executes update
                        stmt1.executeUpdate();

                        Thread.sleep(30 * 1000); //Loop repeats every 30 seconds(30000 milliseconds)

                    } catch (Exception e) {
                        monitor.setLog(new Logger("warn", e.getMessage()));
                    }
                }

            });
            thread.start(); //Starts background task while auto change is 'on'. The loop will end when auto change is set to 'off' in Model.AutoChange

        } catch (Exception e) {

            monitor.setLog(new Logger("warn", e.getMessage()));
        }
    }

    //Auto-change feature for 5 minute intervals
    private void fiveMinsAC() {


        try {
            Thread thread = new Thread(() -> {

                while (Objects.equals(AutoChange.instance.getAuto(), "on")) {
                    String PASSWORD = UserSession.instance.getPasswordHash();
                    try {

                        int leftLimit = 48; // numeral '0'
                        int rightLimit = 122; // letter 'z'
                        int targetStringLength = 20;
                        Random random = new Random();

                        String generatedString = random.ints(leftLimit, rightLimit + 1)
                                //.filter(i -> (i <= 1 || i >= 1) && (i <= 1 || i >= 1)) Option to filter generated string
                                .limit(targetStringLength)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();


                        String password = Cypher.encrypt(generatedString.getBytes(StandardCharsets.UTF_8), PASSWORD);

                        //Hashes password for linked applications
                        String passHashed = Hash.toHexString(Hash.getSHA(generatedString));

                        String query1 = "UPDATE  " + UserSession.instance.getUserName() + " SET password = " + "'" + password + "'" + ", passHashed = " + "'" + passHashed + "'" +" WHERE AutoChange = " + "'" + "5 Minutes" + "'";

                        DatabaseConnection connect1 = new DatabaseConnection();
                        conn = connect1.getVerification();
                        PreparedStatement stmt1 = conn.prepareStatement(query1);

                        stmt1.executeUpdate();

                        Thread.sleep(300 * 1000); //Loop repeats every 5 minutes

                    } catch (Exception e) {
                        monitor.setLog(new Logger("warn", e.getMessage()));
                    }
                }

            });
            thread.start();

        } catch (Exception e) {

            monitor.setLog(new Logger("warn", e.getMessage()));
        }
    }

    //Auto-change feature for 15 minute intervals
    private void fifteenMinsAC() {


        try {
            Thread thread = new Thread(() -> {


                while (Objects.equals(AutoChange.instance.getAuto(), "on")) {
                    String PASSWORD = UserSession.instance.getPasswordHash();
                    try {

                        int leftLimit = 48; // numeral '0'
                        int rightLimit = 122; // letter 'z'
                        int targetStringLength = 20;
                        Random random = new Random();

                        String generatedString = random.ints(leftLimit, rightLimit + 1)
                                //.filter(i -> (i <= 1 || i >= 1) && (i <= 1 || i >= 1)) Option to filter generated string
                                .limit(targetStringLength)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();


                        String password = Cypher.encrypt(generatedString.getBytes(StandardCharsets.UTF_8), PASSWORD);

                        //Hashes password for linked applications
                        String passHashed = Hash.toHexString(Hash.getSHA(generatedString));

                        String query1 = "UPDATE  " + UserSession.instance.getUserName() + " SET password = " + "'" + password + "'" + ", passHashed = " + "'" + passHashed + "'" +" WHERE AutoChange = " + "'" + "15 Minutes" + "'";

                        DatabaseConnection connect1 = new DatabaseConnection();
                        conn = connect1.getVerification();
                        PreparedStatement stmt1 = conn.prepareStatement(query1);

                        stmt1.executeUpdate();

                        Thread.sleep(900 * 1000); //Loop repeats every 15 minutes

                    } catch (Exception e) {
                        monitor.setLog(new Logger("warn", e.getMessage()));
                    }
                }

            });
            thread.start();

        } catch (Exception e) {

            monitor.setLog(new Logger("warn", e.getMessage()));
        }
    }

    //Auto-change feature for 30 minute intervals
    private void thirtyMinsAC() {

        try {
            Thread thread = new Thread(() -> {

                while (Objects.equals(AutoChange.instance.getAuto(), "on")) {
                    String PASSWORD = UserSession.instance.getPasswordHash();
                    try {

                        int leftLimit = 48; // numeral '0'
                        int rightLimit = 122; // letter 'z'
                        int targetStringLength = 20;
                        Random random = new Random();

                        String generatedString = random.ints(leftLimit, rightLimit + 1)
                                //.filter(i -> (i <= 1 || i >= 1) && (i <= 1 || i >= 1)) Option to filter generated string
                                .limit(targetStringLength)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();


                        String password = Cypher.encrypt(generatedString.getBytes(StandardCharsets.UTF_8), PASSWORD);

                        //Hashes password for linked applications
                        String passHashed = Hash.toHexString(Hash.getSHA(generatedString));

                        String query1 = "UPDATE  " + UserSession.instance.getUserName() + " SET password = " + "'" + password + "'" + ", passHashed = " + "'" + passHashed + "'" +" WHERE AutoChange = " + "'" + "30 Minutes" + "'";

                        DatabaseConnection connect1 = new DatabaseConnection();
                        conn = connect1.getVerification();
                        PreparedStatement stmt1 = conn.prepareStatement(query1);

                        stmt1.executeUpdate();

                        Thread.sleep(1800 * 1000); //Loop repeats every 30 minutes

                    } catch (Exception e) {
                        monitor.setLog(new Logger("warn", e.getMessage()));
                    }
                }

            });
            thread.start();

        } catch (Exception e) {

            monitor.setLog(new Logger("warn", e.getMessage()));
        }
    }

    //Auto-change feature for one hour intervals
    private void oneHourAC() {

        try {
            Thread thread = new Thread(() -> {


                while (Objects.equals(AutoChange.instance.getAuto(), "on")) {
                    String PASSWORD = UserSession.instance.getPasswordHash();
                    try {

                        int leftLimit = 48; // numeral '0'
                        int rightLimit = 122; // letter 'z'
                        int targetStringLength = 20;
                        Random random = new Random();

                        String generatedString = random.ints(leftLimit, rightLimit + 1)
                                //.filter(i -> (i <= 1 || i >= 1) && (i <= 1 || i >= 1)) Option to filter generated string
                                .limit(targetStringLength)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();


                        String password = Cypher.encrypt(generatedString.getBytes(StandardCharsets.UTF_8), PASSWORD);

                        //Hashes password for linked applications
                        String passHashed = Hash.toHexString(Hash.getSHA(generatedString));

                        String query1 = "UPDATE  " + UserSession.instance.getUserName() + " SET password = " + "'" + password + "'" + ", passHashed = " + "'" + passHashed + "'" +" WHERE AutoChange = " + "'" + "1 Hour" + "'";
                        DatabaseConnection connect1 = new DatabaseConnection();
                        conn = connect1.getVerification();
                        PreparedStatement stmt1 = conn.prepareStatement(query1);

                        stmt1.executeUpdate();

                        Thread.sleep(3600 * 1000); //Loop repeats every 1 hour

                    } catch (Exception e) {
                        monitor.setLog(new Logger("warn", e.getMessage()));
                    }
                }

            });
            thread.start();

        } catch (Exception e) {

            monitor.setLog(new Logger("warn", e.getMessage()));
        }
    }

    @FXML
    void exitBAction(ActionEvent event){

        Stage stage = (Stage) exitB.getScene().getWindow();
        stage.close();
        monitor.setLog(new Logger("info", "Exiting Application"));

        System.exit(0);
    }
}