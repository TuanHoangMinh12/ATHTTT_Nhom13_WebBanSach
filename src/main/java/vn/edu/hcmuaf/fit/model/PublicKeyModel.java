package vn.edu.hcmuaf.fit.model;

import java.sql.Timestamp;

public class PublicKeyModel {
    private int idKey;
    private int idUser;
    private String userName;   // firstName + lastName (JOIN từ customer)
    private String email;
    private String publicKey;
    private int status;        // 1 = Active, 0 = Revoked
    private Timestamp createDate;
    private Timestamp expire;

    public int getIdKey() { return idKey; }
    public void setIdKey(int idKey) { this.idKey = idKey; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getStatusLabel() {
        return status == 1 ? "Active" : "Revoked";
    }

    public Timestamp getCreateDate() { return createDate; }
    public void setCreateDate(Timestamp createDate) { this.createDate = createDate; }

    public Timestamp getExpire() { return expire; }
    public void setExpire(Timestamp expire) { this.expire = expire; }

    /** Rút gọn public key để hiển thị trong bảng (32 ký tự đầu + ...) */
    public String getPublicKeyShort() {
        if (publicKey == null || publicKey.length() <= 32) return publicKey;
        return publicKey.substring(0, 32) + "...";
    }
}