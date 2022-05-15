package model;


public class UserSession {

    public static UserSession instance;
    public static String userName;
    public static String pass;



    public UserSession(String userName, String passwordHash) {
        UserSession.userName = userName;
        UserSession.pass = passwordHash;

    }


    public static UserSession getInstance(String userName, String passwordHash) {

        instance = new UserSession(userName, passwordHash);

        return instance;
    }


    public String getUserName() {
        return userName;
    }
    public String getPasswordHash() {
        return pass;
    }

    public static void cleanUserSession() {
        userName = "";
        pass = "";
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + userName +
                "pass'" + pass +
                '}';
    }
}