package dragonpass.project;

import Crypto.Hash;
import Crypto.TwoFactorAuthentication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Logger;
import model.UserSession;
import monitor.FileObserver;
import monitor.Observer;
import monitor.SQLiteObserver;
import monitor.Subject;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class LockController {




    @FXML
    private PasswordField pinTF;

    @FXML
    private Button submitBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private PasswordField tfaTF;

    @FXML
    private Label errorLabel;

    int counter = 0;

    Subject monitor = new Subject();
    Observer observe1 = new SQLiteObserver(monitor);
    Observer observe2 = new FileObserver(monitor);

    public void submitBtnAction(ActionEvent event) throws NoSuchAlgorithmException {


        //If/else statements to prevent user from putting in wrong or empty details

        //Pin field blank
        if(pinTF.getText().isBlank()){

            errorLabel.setText("Please enter your pin!");

        }

        //2FA field blank
        else if(tfaTF.getText().isBlank()){

            errorLabel.setText("Please enter your TFA key!");

        }
        else{

            //If all text fields are filled, proceed to log in verification

            /* Note: Unlike registration, there is no need for further parameters in this process as the use of
            prepared statements will only accept credentials that match existing users from the database in the verification process */


            lockVerification();

        }
    }

    private void lockVerification() throws NoSuchAlgorithmException {

        //setting user input to pin
        String pin = "";
        pin= Hash.toHexString(Hash.getSHA(pinTF.getText()));

        //setting user input to 2FA key
        String key = "";
        key=tfaTF.getText();

        String secretKey = "";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs;

        if(counter == 2) {

            UserSession.cleanUserSession(); //User session closed

            monitor.setLog(new Logger("info", "User Session Closed"));

            Stage stage = (Stage) submitBtn.getScene().getWindow();
            stage.close();
            monitor.setLog(new Logger("info", "Too many incorrect login attempts"));

        }
        else {

            try {

                monitor.setLog(new Logger("info", "Lock Verification Requested"));

                //Creating a prepared statement to search and verify existence of user based on user input
                String query = "select * from user_account where username = " + "'" + UserSession.instance.getUserName() + "'" + " AND pin = ?";

                //connecting to database
                DatabaseConnection connect = new DatabaseConnection();
                conn = connect.getVerification();
                stmt = conn.prepareStatement(query);

                //Parameters for query
                stmt.setString(1, pin);


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


                            try {

                                //Close stage
                                Stage stage = (Stage) submitBtn.getScene().getWindow();
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



                            errorLabel.setText("Incorrect. Login attempts remaining: " + result);

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


                    errorLabel.setText("Incorrect. Login attempts remaining: " + result);

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


    }

