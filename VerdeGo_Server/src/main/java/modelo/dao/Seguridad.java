package modelo.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Seguridad {

    /**
     * Convierte un texto plano en un Hash SHA-256
     * @param password Contraseña original (ej: "1234")
     * @return Hash hexadecimal (ej: "03ac6742...")
     */
    public static String hashearPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}