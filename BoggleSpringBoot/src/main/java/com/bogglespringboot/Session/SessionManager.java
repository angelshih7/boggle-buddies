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
        String username = requireValue(request.getUsername(), "username");
        String sessionCode = requireValue(request.getSessionCode(), "sessionCode");

        Session joinSession = sessionRepository.findBySessionCode(sessionCode)
                .orElseGet(() -> new Session(sessionCode, username));

        joinSession.addUser(username);
        return sessionRepository.save(joinSession);
    }

    @PostMapping("/api/session/guest")
    public SessionResponse createGuestSession(@RequestBody GuestSessionRequest request) {
        String sessionCode = requireValue(request.getSessionCode(), "sessionCode");
        String requestedUsername = request.getUsername();

        String baseUsername = (requestedUsername == null || requestedUsername.isBlank()) ? "guest" : requestedUsername.trim();
        String username = uniqueGuestUsername(baseUsername);
        String email = "guest+" + UUID.randomUUID() + "@boggle.local";
        String passwordHash = "GUEST-" + UUID.randomUUID();

        User guestUser = userRepository.save(new User(username, email, passwordHash));

        Session session = sessionRepository.findBySessionCode(sessionCode)
                .orElseGet(() -> new Session(sessionCode, guestUser.getUsername()));
        session.addUser(guestUser.getUsername());
        Session savedSession = sessionRepository.save(session);

        return new SessionResponse(
                savedSession.getId(),
                savedSession.getSessionCode(),
                savedSession.getUsers(),
                guestUser.getId(),
                true
        );
    }

    @PostMapping("/api/session/login")
    public SessionResponse createLoginSession(@RequestBody LoginSessionRequest request) {
        String email = requireValue(request.getEmail(), "email");
        String passwordHash = requireValue(request.getPasswordHash(), "passwordHash");
        String sessionCode = requireValue(request.getSessionCode(), "sessionCode");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid credentials"));

        if (!user.getPasswordHash().equals(passwordHash)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
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
