package vn.edu.hcmuaf.edu.vn.utils;


import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {

    KeyPair keyPair;
    PublicKey publicKey;
    PrivateKey privateKey;

    public void genKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public String getPublicKeyAsString() {
        // Lấy publicKey từ đối tượng RSA của bạn
        PublicKey publicKey = this.publicKey;

        // Chuyển publicKey thành dạng byte[]
        byte[] publicKeyBytes = publicKey.getEncoded();

        // Chuyển publicKey thành chuỗi để chia sẻ
        String publicKeyString = Base64.getEncoder().encodeToString(publicKeyBytes);

        return publicKeyString;
    }

    public String getPrivateKeyAsString() {
        // Lấy publicKey từ đối tượng RSA của bạn
        PrivateKey privateKey = this.privateKey;

        // Chuyển privateKey thành dạng byte[]
        byte[] privateKeyBytes = privateKey.getEncoded();

        // Chuyển publicKey thành chuỗi để chia sẻ
        String privateKeyString = Base64.getEncoder().encodeToString(privateKeyBytes);

        return privateKeyString;
    }

    // Hàm để thiết lập PublicKey từ chuỗi
    public void setPublicKey(String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Chuyển khóa công khai
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicK = keyFactory.generatePublic(publicKeySpec);
        this.publicKey = publicK;
    }

    // Hàm để thiết lập PrivateKey từ chuỗi
    public void setPrivateKey(String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Chuyển khóa riêng tư
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateK = keyFactory.generatePrivate(privateKeySpec);
        this.privateKey = privateK;
    }

    public void setKey(String publicKeyString, String privateKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Chuyển khóa công khai
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicK = keyFactory.generatePublic(publicKeySpec);
        this.publicKey = publicK;

        // Chuyển khóa riêng tư
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateK = keyFactory.generatePrivate(privateKeySpec);
        this.privateKey = privateK;
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] output = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(output);
    }

    public String decrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(output, StandardCharsets.UTF_8);
    }
}