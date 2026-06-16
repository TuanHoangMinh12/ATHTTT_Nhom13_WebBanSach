package vn.edu.hcmuaf.fit.dao;

import java.math.BigInteger;
import java.security.MessageDigest;

public class SHA {
    public static String SHA_256 = "SHA256";


    public String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA_256);

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger number = new BigInteger(1, messageDigest);
            String hashText = number.toString(16);

            return hashText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
