package com.bogglespringboot.Session;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

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

    // Constructor assumes session is started by one user which
    // should immediately be added to the session they created
    public Session(String sessionCode, String createdByUser) {
        users.add(createdByUser);
        this.sessionCode = sessionCode;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

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
