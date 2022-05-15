module dragonpass.project {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.java;
    requires java.desktop;
    requires commons.codec;
    requires totp;
    requires java.prefs;


    opens dragonpass.project to javafx.fxml;
    opens model to javafx.fxml;
    exports dragonpass.project;
    exports model;
    exports Crypto;
    opens Crypto to javafx.fxml;
}