package com.bogglespringboot.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
Represents a persisted multiplayer session mapped to the sessions table.
Stores the session code and the usernames currently joined to that session.
 */
@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_code", nullable = false, unique = true, length = 50)
    private String sessionCode;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "session_users", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "username", nullable = false, length = 50)
    private List<String> users = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Session() {
    }

    // Track submitted words per user (lowercased, trimmed)
    Map<String, Set<String>> submittedWordsByUser = new HashMap<>();

    // Constructor assumes session is started by one user which
    // should immediately be added to the session they created
    public Session(String sessionCode, String createdByUser) {
        users.add(createdByUser);
        submittedWordsByUser.put(createdByUser, new HashSet<>());
        this.sessionCode = sessionCode;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
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

    public List<String> getUsers() {
        return users;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addUser(String username) {
        if (!users.contains(username)) {
            users.add(username);
        }
    }
}
