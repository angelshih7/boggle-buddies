package com.bogglespringboot.Session;

import java.util.ArrayList;

// Class to store details of one active session.
// May be replaced by database queries in the future?
public class Session {
    // Some session specifier required
    String sessionCode;
    // List of users in session
    ArrayList<String> users = new ArrayList<String>();

    // Constructor assumes session is started by one user which
    // should immediately be added to the session they created
    Session(String sessionCode, String createdByUser) {
        users.add(createdByUser);
        this.sessionCode = sessionCode;
    }

    // Jackson (Spring tool that converts to/from JSON request format) needs getters
    public String getSessionCode() {
        return sessionCode;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
