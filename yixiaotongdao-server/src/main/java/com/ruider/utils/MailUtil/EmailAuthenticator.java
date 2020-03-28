package com.ruider.utils.MailUtil;

import javax.mail.*;

public class EmailAuthenticator extends Authenticator{

    private String userName;
    private String password;

    public EmailAuthenticator(){}

    public EmailAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(userName, password);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
