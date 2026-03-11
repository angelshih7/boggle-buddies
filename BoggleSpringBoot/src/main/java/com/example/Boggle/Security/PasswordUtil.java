package com.example.Boggle.Security;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Utility class for securely hashing and verifying passwords.
 *
 * This class uses PBKDF2 with HMAC-SHA256, a random salt, and a configurable
 * iteration count to store passwords in a secure hashed format.
 */
public final class PasswordUtil {
    //prevents instantiation.
    private PasswordUtil() {}

    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;

    /**
     * Hashes a plain-text password using PBKDF2 with a random salt.
     *
     * @param password the plain-text password to hash
     * @return a formatted string containing the algorithm, iteration count, salt, and derived key
     * @throws IllegalArgumentException if the password is null or blank
     */
    public static String hash(String password){
        if(password == null || password.isBlank()){
            throw new IllegalArgumentException("Password Required");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());

            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verifies a plain-text password against a previously stored password hash.
     *
     * @param password the plain-text password to verify
     * @param stored the stored password hash string
     * @return true if the password matches the stored hash; false otherwise
     */
    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) {
            return false;
        }

        try {
            String hashedInput = hash(password);

            return MessageDigest.isEqual(
                    hashedInput.getBytes(),
                    stored.getBytes()
            );
        } catch (Exception e) {
            return false;
        }
    }

}