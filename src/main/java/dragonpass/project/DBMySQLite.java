package dragonpass.project;

import model.Logger;
import monitor.FileObserver;
import monitor.Observer;
import monitor.Subject;
import java.sql.*;


public class DBMySQLite {
    static Subject monitor = new Subject();
    Observer observe2 = new FileObserver(monitor);

    private static DBMySQLite firstInstance = null;
    private static String url = "jdbc:sqlite:log.db";

    private DBMySQLite() {
        try (Connection conn = DriverManager.getConnection(url)) {
            seedDB();

        } catch (SQLException e) {

            monitor.setLog(new Logger("warn", e.getCause().getMessage()));
        }
    }

    public static DBMySQLite getInstance() {
        if (firstInstance == null) {
            firstInstance = new DBMySQLite();
        }
        return firstInstance;
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS monitor (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	level text NOT NULL,\n"
                + "	message text,\n"
                + " occuranceTime text \n "
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            monitor.setLog(new Logger("warn", e.getCause().getMessage()));
        }
    }

    public static void seedDB() {
        createTable();
        createTable();
    }

    public static void insert(Logger log) {
        String sql = "INSERT INTO monitor(level,message, occuranceTime) VALUES(?,?,?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.getLevel());
            ps.setString(2, log.getMessage());
            ps.setString(3, log.getOccurenceTime().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            monitor.setLog(new Logger("warn", e.getCause().getMessage()));
        }
    }
}
