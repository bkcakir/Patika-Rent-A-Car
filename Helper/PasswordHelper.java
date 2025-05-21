package Helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHelper {
    
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algoritması bulunamadı", e);
        }
    }
    
    public static String createHashedPassword(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return salt + "$$" + hashedPassword;
    }
    
    public static boolean verifyPassword(String password, String storedPassword) {
        String[] parts = storedPassword.split("\\$\\$");
        if (parts.length != 2) {
            return false;
        }
        String salt = parts[0];
        String hashedPassword = parts[1];
        String newHashedPassword = hashPassword(password, salt);
        return hashedPassword.equals(newHashedPassword);
    }
} 