package bgu.spl.net.api;

public class User {

    private String userName ;
    private String passcode ;
    private boolean isLogged ;

    public User(String userName, String passcode) {
        this.userName = userName;
        this.passcode = passcode;
        this.isLogged = true;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }
}
