package com.bogglespringboot.Model.Controllers;

import com.bogglespringboot.Model.Tables.User;
import com.bogglespringboot.Security.PasswordUtil;
import com.bogglespringboot.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

/**
 *
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private static final SecureRandom rng = new SecureRandom();

    /**
     *
     * @param userRepository
     */
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //Data transfer objects

    /**
     *
     */
    public static class RegisterRequest{
        public String username;
        public String email;
        public String password;
    }

    public static class LoginRequest{
        public String username;
        public String password;
    }

    public static class GuestRequest{
        public String username;
    }

    public static class UserResponse{
        public Integer id;
        public String username;
        public String email;
        public static UserResponse userDTO(User u){
            UserResponse out = new UserResponse();
            out.id = u.getId();
            out.username = u.getUsername();
            out.email = u.getEmail();

            return out;
        }
    }

    //----Endpoint for user creation
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserResponse register (@RequestBody RegisterRequest req) {
        if(req == null) throw new ResponseStatusException(BAD_REQUEST,"Body is required");
        String username = require(req.username,"username");
        String email = require(req.email,"email");
        String password = require(req.password,"password");

        if(userRepository.existsByUsername(username)){
            throw new ResponseStatusException(CONFLICT,"Username already taken.");
        }
        if(userRepository.findByEmail(email).isPresent()){
            throw new ResponseStatusException(CONFLICT,"Email Already Taken");
        }

        String passwordHash = PasswordUtil.hash(password);

        try{
            User u = new User(username, email, passwordHash);
            u.setGuest(false);
            u = userRepository.save(u);
            return UserResponse.userDTO(u);
        } catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(CONFLICT, "Username or email already taken");
        }
    }
    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest req) {
        if (req == null) throw new ResponseStatusException(BAD_REQUEST, "Body is required");
        String username = require(req.username, "username");
        String password = require(req.password, "password");

        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid username or password"));

        // Optional policy: guests can't log in with password
        if (u.isGuest()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid username or password");
        }

        if (!PasswordUtil.verify(password, u.getPasswordHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid username or password");
        }

        return UserResponse.userDTO(u);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/guest")
    public UserResponse guest(@RequestBody(required = false) GuestRequest req) {
        String desired = (req == null) ? "" : safe(req.username);
        String base = desired.isBlank() ? generateGuestUsername() : desired.trim();

        String username = makeUniqueUsername(base);

        // DB requires NOT NULL email + password_hash :contentReference[oaicite:4]{index=4}
        String email = "guest_" + UUID.randomUUID() + "@guest.local";

        // Guests won't use passwords, but store a valid hash format anyway.
        String passwordHash = PasswordUtil.hash(UUID.randomUUID().toString());

        try {
            User u = new User(username, email, passwordHash);
            u.setGuest(true);
            u = userRepository.save(u);
            return UserResponse.userDTO(u);
        } catch (DataIntegrityViolationException e) {
            String retryUsername = makeUniqueUsername("Guest-" + shortToken());
            User u = new User(retryUsername, "guest_" + UUID.randomUUID() + "@guest.local",
                    PasswordUtil.hash(UUID.randomUUID().toString()));
            u.setGuest(true);
            u = userRepository.save(u);
            return UserResponse.userDTO(u);
        }
    }

    //-----helper methods

    private String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, field + " is required");
        }
        return value.trim();
    }

    private String safe(String s) { return s == null ? "" : s; }


    private String makeUniqueUsername(String base) {
        String candidate = base;
        int tries = 0;
        while (userRepository.existsByUsername(candidate)) {
            tries++;
            if (tries > 25) return base + "-" + UUID.randomUUID();
            candidate = base + "-" + shortToken();
        }
        return candidate;
    }

    /**
     *
     * @return
     */
    private String generateGuestUsername() { return "Guest-" + shortToken(); }

    private String shortToken() {
        byte[] b = new byte[3];
        rng.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}
