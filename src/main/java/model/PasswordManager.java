package model;

public class PasswordManager {

    String app;
    String username;
    String password;
    String autoChange;



    public PasswordManager(String app, String username, String password, String autoChange) {
        this.app = app;
        this.username = username;
        this.password = password;
        this.autoChange = autoChange;


    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAutoChange() {
        return autoChange;
    }

    public void setAutoChange(String autoChange) {
        this.autoChange = autoChange;
    }

}
