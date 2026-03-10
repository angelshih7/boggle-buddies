package com.example.Boggle.Session;

public class SubmitWordResponse {
    private boolean accepted;
    private String reason; // "OK" or "DUPLICATE" or "INVALID"

    public SubmitWordResponse() {}

    public SubmitWordResponse(boolean accepted, String reason) {
        this.accepted = accepted;
        this.reason = reason;
    }

    public boolean isAccepted() { return accepted; }
    public String getReason() { return reason; }

    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    public void setReason(String reason) { this.reason = reason; }
}