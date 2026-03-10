package com.example.Boggle.Session;

public class SubmitWordRequest {
    private String username;
    private String sessionCode;
    private String word;

    public SubmitWordRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
}