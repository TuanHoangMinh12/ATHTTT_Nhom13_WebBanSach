package vn.edu.hcmuaf.fit.utils;


import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.PublicKeyDao;

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

    private static CartDao cartDao = new CartDao();
    private static SHA256Util sha256Util = new SHA256Util();
    private static ObjectVerifyUtil objUtil = new ObjectVerifyUtil();
    private static PublicKeyDao publicKeyDao = new PublicKeyDao();

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


    public static String autoVerifyOrder(int idCart, int idUser) {
        try {
            String signature = cartDao.getHash(idCart, idUser);
            String publicKey = cartDao.getPuclickey(idUser, idCart);

            if (signature == null || signature.trim().isEmpty() || "NULL".equalsIgnoreCase(signature)) {
                return null;
            }
            if (publicKey == null) {
                System.out.println(" Không tìm thấy Public Key cho User ID: " + idUser);
                return "FAIL";
            }

            // Sinh chuỗi kiểm tra từ dữ liệu hiện tại trong DB của Admin
            String orderString = objUtil.string(idUser, idCart);
            String hash1 = sha256Util.check(orderString);

            // LOG ĐỂ ĐỐI CHIẾU: Bật console lên so sánh xem 2 dòng này có khớp hoàn toàn với lúc User tạo đơn không
            System.out.println("[ADMIN VERIFY] - Chuỗi gốc đơn hàng: " + orderString);
            System.out.println("[ADMIN VERIFY] - Mã Hash tính toán lại: " + hash1);

            // Tiến hành xác thực chữ ký
            String cleanPublicKey = publicKey.trim().replace("\r\n", "").replace("\n", "");
            byte[] publicBytes = Base64.getDecoder().decode(cleanPublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            String cleanSignature = signature.trim().replace("\r\n", "").replace("\n", "");
            byte[] signatureBytes = Base64.getDecoder().decode(cleanSignature);

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(pubKey);

            // Truyền mã hash1 vào để Verify
            sig.update(hash1.getBytes("UTF-8"));

            boolean isCorrect = sig.verify(signatureBytes);

            if (isCorrect) {
                System.out.println("Kết quả kiểm tra chữ ký: " + (isCorrect ? "HỢP LỆ (OK)" : "KHÔNG KHỚP (FAIL)"));
                return "OK";
            } else {
                // TỰ ĐỘNG CẬP NHẬT TRẠNG THÁI HỦY ĐƠN HÀNG XUỐNG DATABASE
                cartDao.updateCart(idCart, 4);
                System.out.println(" Đơn hàng #" + idCart + " có chữ ký Invalid. Đã tự động chuyển trạng thái về Đã hủy (4).");
                return "FAIL";
            }

        } catch (Exception e) {
            System.err.println("Lỗi xác thực hệ thống: " + e.getMessage());
            return "FAIL";
        }
    }
}