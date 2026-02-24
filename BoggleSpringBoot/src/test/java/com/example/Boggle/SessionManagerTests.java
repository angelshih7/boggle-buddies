package com.example.Boggle;

import com.example.Boggle.sessions.Session;
import com.example.Boggle.sessions.SessionManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SessionManagerTests {
    @Autowired
    // This is the Spring-owned instance of the sessionManager.
    private SessionManager sessionManager;

    @LocalServerPort
    int port;

    @Test
    // Creates one session with one user and checks that code and username were saved
    void testCreateNewSession() throws Exception {
        // Mock client for testing purposes
        HttpClient client = HttpClient.newHttpClient();

        // Make request with JSON corresponding to Session member variables
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/join"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"username\":\"testUser\", \"sessionCode\":\"mySessionCode\"}"
                ))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        client.close();

        // Check that our request was acknowledged
        assertEquals(200, response.statusCode());
        // Check that username/session code were stored
        Session testSession = sessionManager.getActiveSessions().getFirst();
        assertEquals("mySessionCode", testSession.getSessionCode());
        assertEquals("testUser", testSession.getUsers().getFirst());
    }
}