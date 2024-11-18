package guru.qa.niffler.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class OauthUtils {

    private static final int DEFAULT_VERIFIER_LENGTH = 43;
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateCodeVerifier() {
        return generateCodeVerifier(DEFAULT_VERIFIER_LENGTH);
    }

    public static String generateCodeVerifier(int length) {
        if (length < 43 || length > 128) {
            throw new IllegalArgumentException("Code verifier length must be between 43 and 128 characters");
        }
        StringBuilder codeVerifier = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            codeVerifier.append(ALLOWED_CHARS.charAt(secureRandom.nextInt(ALLOWED_CHARS.length())));
        }
        return codeVerifier.toString();
    }

    public static String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(codeVerifier.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
