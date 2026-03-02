package com.bogglespringboot.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Class to store details of one active session.
// May be replaced by database queries in the future?
public class Session {
    // Some session specifier required
    String sessionCode;
    // List of users in session
    ArrayList<String> users = new ArrayList<String>();

    // Track submitted words per user (lowercased, trimmed)
    Map<String, Set<String>> submittedWordsByUser = new HashMap<>();

    // Constructor assumes session is started by one user which
    // should immediately be added to the session they created
    Session(String sessionCode, String createdByUser) {
        users.add(createdByUser);
        submittedWordsByUser.put(createdByUser, new HashSet<>());
        this.sessionCode = sessionCode;
    }

    // Add user helper
    void addUser(String username) {
        if (!users.contains(username)) {
            users.add(username);
        }
        submittedWordsByUser.putIfAbsent(username, new HashSet<>());
    }

    // Returns true if word is new, false if duplicate
    boolean recordWord(String username, String wordRaw) {
        if (username == null || wordRaw == null) return false;

        String word = wordRaw.trim().toLowerCase();
        if (word.isEmpty()) return false;

        submittedWordsByUser.putIfAbsent(username, new HashSet<>());
        Set<String> submitted = submittedWordsByUser.get(username);

        if (submitted.contains(word)) return false;

        submitted.add(word);
        return true;
    }

    // Jackson (Spring tool that converts to/from JSON request format) needs getters
    public String getSessionCode() {
        return sessionCode;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
