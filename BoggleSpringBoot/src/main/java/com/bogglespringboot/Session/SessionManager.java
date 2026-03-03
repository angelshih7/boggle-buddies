package com.bogglespringboot.Session;

import com.bogglespringboot.Model.Tables.User;
import com.bogglespringboot.repository.SessionRepository;
import com.bogglespringboot.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
public class SessionManager {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SessionManager(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

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

        Session session = sessionRepository.findBySessionCode(sessionCode)
                .orElseGet(() -> new Session(sessionCode, user.getUsername()));
        session.addUser(user.getUsername());
        Session savedSession = sessionRepository.save(session);

        return new SessionResponse(
                savedSession.getId(),
                savedSession.getSessionCode(),
                savedSession.getUsers(),
                user.getId(),
                false
        );
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
        return sessionRepository.findBySessionCode(sessionCode).orElseThrow(NoSuchElementException::new);
    }

    // Getter to access active sessions for testing purposes
    public ArrayList<Session> getActiveSessions() {
        return new ArrayList<>(sessionRepository.findAll());
    }

    private String requireValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " is required");
        }
        return value.trim();
    }

    private String uniqueGuestUsername(String baseUsername) {
        if (!userRepository.existsByUsername(baseUsername)) {
            return baseUsername;
        }

        int suffix = 1;
        String candidate = baseUsername + suffix;
        while (userRepository.existsByUsername(candidate)) {
            suffix++;
            candidate = baseUsername + suffix;
        }
        return candidate;
    }
}
