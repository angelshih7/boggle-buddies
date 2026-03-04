package com.bogglespringboot.Session;

public class JoinSessionRequest {

    private String username;
    private String sessionCode;

    public JoinSessionRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }
}