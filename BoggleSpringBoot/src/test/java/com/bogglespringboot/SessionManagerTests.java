package com.bogglespringboot;

import com.bogglespringboot.Model.Tables.User;
import com.bogglespringboot.Session.Session;
import com.bogglespringboot.repository.SessionRepository;
import com.bogglespringboot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SessionManagerTests {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

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

        // Check that our request was acknowledged
        assertEquals(200, response.statusCode());
        // Check that username/session code were persisted
        Session testSession = sessionRepository.findBySessionCode("mySessionCode").orElseThrow();
        assertEquals("mySessionCode", testSession.getSessionCode());
        assertTrue(testSession.getUsers().contains("testUser"));
    }

    @Test
    void testCreateGuestSessionPersistsGuestUserAndSession() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/session/guest"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"username\":\"guestUser\", \"sessionCode\":\"guest-room\"}"
                ))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(userRepository.findByUsername("guestUser").isPresent());

        Session testSession = sessionRepository.findBySessionCode("guest-room").orElseThrow();
        assertTrue(testSession.getUsers().contains("guestUser"));
    }

    @Test
    void testLoginSessionPersistsJoinedUser() throws Exception {
        User user = userRepository.save(new User("registeredUser", "registered@example.com", "hash123"));

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/session/login"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"email\":\"registered@example.com\", \"passwordHash\":\"hash123\", \"sessionCode\":\"login-room\"}"
                ))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Session testSession = sessionRepository.findBySessionCode("login-room").orElseThrow();
        assertTrue(testSession.getUsers().contains(user.getUsername()));
    }
}
