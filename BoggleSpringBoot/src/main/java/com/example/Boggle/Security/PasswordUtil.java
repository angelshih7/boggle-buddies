import BoggleSpringBoot.src.main.java.com.example.Boggle.Security.PasswordUtil.java
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Utility class for hashing and verifying passwords using SHA-256.
 *
 * This implementation is intentionally simplified for educational purposes.
 * It performs a single SHA-256 hash with no salt or key stretching. In real
 * applications, stronger password hashing algorithms such as PBKDF2, bcrypt,
 * scrypt, or Argon2 should be used.
 */
public final class PasswordUtil {

    /**
     * Private constructor to prevent instantiation.
     */
    private PasswordUtil() {}

    /**
     * Hashes a password using the SHA-256 algorithm.
     *
     * The resulting hash is encoded using Base64 so it can be easily stored
     * as a string in a database.
     *
     * @param password the plaintext password to hash
     * @return the Base64-encoded SHA-256 hash of the password
     * @throws IllegalArgumentException if the password is null or blank
     * @throws RuntimeException if the hashing process fails
     */
    public static String hash(String password) {
        if (password == null || password.isBlank()) {
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
     * Verifies a plaintext password against a stored SHA-256 hash.
     *
     * The password provided by the user is hashed using the same SHA-256
     * process and compared to the stored hash using a constant-time comparison
     * to reduce timing attack risks.
     *
     * @param password the plaintext password to verify
     * @param stored the previously stored Base64-encoded hash
     * @return true if the password matches the stored hash, false otherwise
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