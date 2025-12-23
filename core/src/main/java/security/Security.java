package security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Security {

    public static String hashString(String s){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(s.getBytes());

            return toHexString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hashString(String s, int salt){
        return hashString(s + salt);
    }

    private static String toHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        Formatter formatter = new Formatter(sb);

        for(byte b : bytes)
            formatter.format("%02x", b);

        return sb.toString();
    }
}
