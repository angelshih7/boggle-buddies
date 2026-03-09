package com.bogglespringboot.Security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
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

    private static final SecureRandom RNG = new SecureRandom(); // used to generate random byte for hashing
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
        try{
            byte[] salt = new byte[SALT_BYTES];
            RNG.nextBytes(salt);

            byte[] dk = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_BITS);

            return "pbkdf2:" + ITERATIONS + ":" +
                    Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(dk);
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
        if (password == null || stored == null) return false;

        try {
            String[] parts = stored.split(":");
            if (parts.length != 4) return false;
            if (!"pbkdf2".equals(parts[0])) return false;

            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);

            byte[] actual = pbkdf2(password.toCharArray(), salt, iterations, expected.length * 8);
            return MessageDigest.isEqual(actual, expected);
        } catch (Exception e) {
            return false; // treat parse/crypto errors as "no match"
        }
    }

    /**
     * Derives a key from a password using PBKDF2 with HMAC-SHA256.
     *
     * @param password the password characters
     * @param salt the salt bytes
     * @param iterations the number of PBKDF2 iterations
     * @param keyBits the desired derived key length in bits
     * @return the derived key bytes
     * @throws Exception if key derivation fails
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyBits) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyBits);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();

    }

}
