package dragonpass.project;
import Crypto.Hash;
import javafx.scene.control.*;
import model.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import monitor.FileObserver;
import monitor.Observer;
import monitor.SQLiteObserver;
import monitor.Subject;
import org.apache.commons.codec.binary.Base32;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import java.security.SecureRandom;
import java.sql.*;


public class RegisterController {

    Subject monitor = new Subject();
    Observer observe1 = new SQLiteObserver(monitor);
    Observer observe2 = new FileObserver(monitor);

    @FXML
    private Button closeBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField passTF;

    @FXML
    private PasswordField confirmPassTF;

    @FXML
    private PasswordField pinTF;

    @FXML
    private PasswordField confirmPinTF;

    @FXML
    private Label pinMatch;

    @FXML
    private TextField usernameTF;

    @FXML
    private TextField emailTF;

    @FXML
    private Label usernameError;

    @FXML
    private Label emailError;

    @FXML
    private Label passwordError;

    @FXML
    private Label tfaError;

    @FXML
    private TextArea secretKey;


    public void closeBtnAction() {

        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();

        try {

            monitor.setLog(new Logger("info", "Exit application"));
            System.exit(0);

        } catch (Exception e) {

            monitor.setLog(new Logger("warn", e.getMessage()));
            System.exit(0);

        }

    }

    public void loginBtnAction(ActionEvent event) {

        Stage stage = (Stage) loginBtn.getScene().getWindow();
        stage.close();

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 500);
            stage.setTitle("Dragon Pass");
            stage.setScene(scene);
            stage.show();
            monitor.setLog(new Logger("info", "login controller accessed"));

        } catch (Exception e) {

            monitor.setLog(new Logger("warn", e.getMessage()));

        }

    }


    //Email parameters verification
    public static boolean emailV(String email) {
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+.+$";
        return email.matches(regex);
    }

    //Submission Button
        public void registerBtnAction(ActionEvent event) throws SQLException, NoSuchAlgorithmException {
            String userEmail = emailTF.getText();
            Boolean validUsername = null;
            Boolean validEmail = null;
            Boolean validPass = null;
            Boolean validPin = null;
            boolean emailV = RegisterController.emailV(userEmail);
            Boolean validKey = null;

            //username errors
            if(usernameTF.getText().isBlank()){

                usernameError.setText("Please enter your username!");

            }
            else if(usernameTF.getText().length() > 15){

                usernameError.setText("Username must be less than 15 characters!");

            }
            else if(usernameTF.getText().contains(" ")){

                usernameError.setText("Username must not contain any spaces!");

            }

            else{

                usernameError.setText("✔");
                validUsername = true;

            }

            //Email errors
            if(emailTF.getText().isBlank()){

                emailError.setText("Please enter your email!");

            }
            else if(emailTF.getText().length() > 18){

                emailError.setText("Email must be 18 characters or less!");
            }
            else if(!emailV){
                emailError.setText("invalid email!");
            }
            else{

                emailError.setText("✔");
                validEmail = true;

            }

            //Password errors
            if(passTF.getText().isBlank() || confirmPassTF.getText().isBlank()){

                passwordError.setText("Please enter your password in both fields!");

            }
            else if(!(passTF.getText().length() <= 20)) {

                passwordError.setText("Password must be 20 characters or less");

            }
            else if(!passTF.getText().equals(confirmPassTF.getText())){

                passwordError.setText("Passwords do not match!");

            }
            else{

                passwordError.setText("✔");
                validPass = true;

            }


            //Pin errors
            //Pin match

            if(pinTF.getText().isBlank() || confirmPinTF.getText().isBlank()){

                pinMatch.setText("Please enter your pin in both fields!");

            }
            else if(!(pinTF.getText().length() == 4)){

                pinMatch.setText("Pin must be 4 digits");

            }

           else if(pinTF.getText().equals(confirmPinTF.getText()) && !pinTF.getText().isBlank()) {

                String input = pinTF.getText();
                boolean flag = true;
                for (int a = 0; a < input.length(); a++) {
                    if (a == 0 && input.charAt(a) == '-')
                        continue;
                    if (!Character.isDigit(input.charAt(a)))
                        flag = false;
                    pinMatch.setText("Pin must contain 4 digits!");
                }
                if (flag) {
                    pinMatch.setText("✔");
                    validPin = true;
                }
            }

            else{

                pinMatch.setText("pins do not match!");

            }

            if(secretKey.getText().isBlank()){

                validKey = false;

                tfaError.setText("Please generate a 2FA key and follow the below steps!");

            }
            else{

                validKey = true;
                tfaError.setText("✔");

            }

            if(Boolean.TRUE.equals(validUsername)
            && Boolean.TRUE.equals(validEmail)
            && Boolean.TRUE.equals(validPass)
            && Boolean.TRUE.equals(validPin)
            && Boolean.TRUE.equals(validKey)){
                usernameChecker();
            }

            else{

                monitor.setLog(new Logger("info", "Submission details invalid"));

            }



        }


    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];

        random.nextBytes(bytes);

        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);

    }

    public void genBtnAction(ActionEvent event){

        secretKey.setText(generateSecretKey());

    }



    private void usernameChecker() {

        Connection conn;
        ResultSet rs;
        String username = usernameTF.getText();

        try {

            String query = "select * from user_account where username = ?"; //query to look for existing username

            DatabaseConnection connect = new DatabaseConnection();
            conn = connect.getVerification();
            PreparedStatement stmt = conn.prepareStatement(query);

            //Parameter for query
            stmt.setString(1, username);

            //Executing prepared statement
            rs = stmt.executeQuery();

            if (rs.next()) {

                usernameAlreadyExists();
                monitor.setLog(new Logger("info", "Submission details invalid"));

            } else {

                register();

            }


        } catch (Exception e) {

            monitor.setLog(new Logger("warn", e.getMessage()));

        }
    }

    private void usernameAlreadyExists() {

        usernameError.setText("Username already exists!");

    }

    //Prepared statement on submission
    private void register() throws SQLException, NoSuchAlgorithmException {

        Connection conn;

        String username = usernameTF.getText();
        String email = emailTF.getText();
        String password = Hash.toHexString(Hash.getSHA(passTF.getText()));
        String pin = Hash.toHexString(Hash.getSHA(pinTF.getText()));
        String key = secretKey.getText();


        String query = "INSERT INTO user_account(username, email, password, pin, tfaKey, session) VALUES(?,?,?,?,?,0)";

        DatabaseConnection connect = new DatabaseConnection();
        conn = connect.getVerification();
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, username);
        stmt.setString(2, email);
        stmt.setString(3, password);
        stmt.setString(4, pin);
        stmt.setString(5, key);


        try{
            stmt.execute();
            monitor.setLog(new Logger("info", "New account added"));

            String query2 = "CREATE TABLE IF NOT EXISTS " + username + " (\n" +
                    "                                password_ID INT NOT NULL AUTO_INCREMENT,\n" +
                    "                                app VARCHAR(45) NOT NULL,\n" +
                    "                                username VARCHAR(45) NOT NULL,\n" +
                    "                                password VARCHAR(500) NOT NULL,\n" +
                    "                                passHashed VARCHAR(500) DEFAULT 'null',\n" +
                    "                                AutoChange VARCHAR(20) DEFAULT 'Inactive',\n" +
                    "                                PRIMARY KEY (password_ID),\n" +
                    "                                CONSTRAINT UC_user_account UNIQUE (password_ID)\n" +
                    "                                \n" +
                    "                                );";

            PreparedStatement stmt2 = conn.prepareStatement(query2);
            stmt2.execute();

            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();


                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 800, 500);
                stage.setTitle("Dragon Pass");
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {

                monitor.setLog(new Logger("warn", e.getMessage()));

            }

        finally{
            //Close connection
            try{
                conn.close();
            }
            catch(Exception e){
                monitor.setLog(new Logger("warn", e.getMessage()));
            }
        }

        }




}



