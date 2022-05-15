package dragonpass.project;

import model.Logger;
import monitor.FileObserver;
import monitor.Observer;
import monitor.SQLiteObserver;
import monitor.Subject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class DatabaseConnection {

    Subject monitor = new Subject();
    Observer observe1 = new SQLiteObserver(monitor);
    Observer observe2 = new FileObserver(monitor);

    private Connection conn;
    private String user;
    private String password;
    private static final String dbName = "dragonpass";
    private static final String connection = "jdbc:mysql://localhost:3306/";
    private static final String verification = "jdbc:mysql://localhost:3306/" + dbName;



    //User and Password call for localhost
     DatabaseConnection() {
        Properties p = new Properties();

        try(FileInputStream ip = new FileInputStream("config.properties")){ //User and password set for database access in config.properties

            p.load(ip);
            user = p.getProperty("user");
            password = p.getProperty("password");


        } catch (FileNotFoundException e) {
            monitor.setLog(new Logger("warn", e.getMessage()));
        } catch (IOException e) {
            monitor.setLog(new Logger("warn", e.getMessage()));
        }

    }

    //Connection for generating database if not already created
     public Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                PreparedStatement stmt;

                Properties props = new Properties();
                props.put("user", user);
                props.put("password", password);
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(connection, props);

                //Prepared statement table for database creation
                String sql1 = "CREATE DATABASE IF NOT EXISTS dragonpass";

                //Executes prepared statement
                stmt = conn.prepareStatement(sql1);
                stmt.executeUpdate();

            } catch (Exception e) {
                monitor.setLog(new Logger("warn", e.getMessage()));
            }
        }
        return conn;
    }

    //Connection for submission and status verification
    public Connection getVerification() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {

                PreparedStatement stmt;

                Properties props = new Properties();
                props.put("user", user);
                props.put("password", password);
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(verification, props);

                //Prepared statement table for user_submission if table doesn't already exist
                String sql2 =
                        """
                                CREATE TABLE IF NOT EXISTS user_account (
                                    user_accountID INT NOT NULL AUTO_INCREMENT,
                                    username VARCHAR(45) NOT NULL,
                                    email VARCHAR(45) NOT NULL,
                                    password VARCHAR(500) NOT NULL,
                                    pin VARCHAR(500) NOT NULL,
                                    session INT NOT NULL,
                                    tfaKey VARCHAR(500) NOT NULL,
                                    PRIMARY KEY (user_accountID),
                                    CONSTRAINT UC_user_account UNIQUE (user_accountID, email, username)
                                );""";

                //Executes prepared statement
                stmt = conn.prepareStatement(sql2);
                stmt.execute();

            } catch (Exception e) {
                monitor.setLog(new Logger("warn", e.getMessage()));
            }
        }
        return conn;
    }


}