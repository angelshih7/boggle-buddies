package com.bogglespringboot.Session;

import java.util.List;

public class SessionResponse {
    private Long sessionId;
    private String sessionCode;
    private List<String> users;
    private Integer userId;
    private boolean guest;

    public SessionResponse() {
    }

    public SessionResponse(Long sessionId, String sessionCode, List<String> users, Integer userId, boolean guest) {
        this.sessionId = sessionId;
        this.sessionCode = sessionCode;
        this.users = users;
        this.userId = userId;
        this.guest = guest;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public List<String> getUsers() {
        return users;
    }

    public Integer getUserId() {
        return userId;
    }

    public boolean isGuest() {
        return guest;
    }
}
