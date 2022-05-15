package dragonpass.project;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import monitor.FileObserver;
import monitor.Observer;
import monitor.SQLiteObserver;
import monitor.Subject;
import model.UserSession;


import java.beans.EventHandler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Main extends Application {
    Subject monitor = new Subject();
    Observer observe1 = new SQLiteObserver(monitor);
    Observer observe2 = new FileObserver(monitor);

    Connection conn = null;


    @Override
    public void start(Stage stage) throws IOException, SQLException {

        UserSession.cleanUserSession(); //Cleans user sessions
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml")); //loads login page
        Scene scene = new Scene(fxmlLoader.load(), 750, 436);
        stage.setTitle("Dragon Pass");
        stage.setScene(scene);
        stage.show();
        monitor.setLog(new model.Logger("info", "Application startup successful"));


    }

    public static void main(String[]  args) {
        launch();

    }


    public static Logger LOGGER = Logger.getLogger(Main.class.getName());


}

