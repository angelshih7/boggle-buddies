package com.bogglespringboot.Session;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
public class SessionManager {
    // Might be temporary if we start a session database
    ArrayList<Session> activeSessions = new ArrayList<>();

    // Respond to post requests to join a session with username and a session code
    @PostMapping("/api/join")
    public Session newSession(@RequestBody JoinSessionRequest request) {
        Session joinSession;
        try {
            // Try to find existing session with given code
            joinSession = findSession(request.getSessionCode());
            joinSession.addUser(request.getUsername());
        } catch (NoSuchElementException e) {
            // If no existing session with given code exists, create new
            joinSession = new Session(request.getSessionCode(), request.getUsername());
            activeSessions.add(joinSession);
        }
        return joinSession;
    }

    // New endpoint: submit a word, reject duplicates per (sessionCode, username)
    @PostMapping("/api/submitWord")
    public SubmitWordResponse submitWord(@RequestBody SubmitWordRequest request) {
        if (request.getSessionCode() == null || request.getUsername() == null || request.getWord() == null) {
            return new SubmitWordResponse(false, "INVALID");
        }

        try {
            Session session = findSession(request.getSessionCode());

            // Optional: auto-add user if they somehow submit before joining
            session.addUser(request.getUsername());

            boolean accepted = session.recordWord(request.getUsername(), request.getWord());
            if (!accepted) {
                return new SubmitWordResponse(false, "DUPLICATE");
            }
            return new SubmitWordResponse(true, "OK");

        } catch (NoSuchElementException e) {
            return new SubmitWordResponse(false, "INVALID");
        }
    }

    // Look for active sessions by code, throw NoSuchElementException if none found
    Session findSession(String sessionCode) throws NoSuchElementException {
        // Search for session code in active sessions
        for (Session activeSession : activeSessions) {
            if (activeSession.sessionCode.equals(sessionCode)) {
                // Return session if found
                return activeSession;
            }
        }
        // Session code not found in active sessions
        throw new NoSuchElementException();
    }

    // Getter to access active sessions for testing purposes
    public ArrayList<Session> getActiveSessions() {
        return activeSessions;
    }
}