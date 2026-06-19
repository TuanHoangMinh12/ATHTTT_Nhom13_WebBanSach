package vn.edu.hcmuaf.fit.model;

import java.sql.Timestamp;

/**
 * Model đại diện cho yêu cầu báo mất khóa private key của người dùng.
 */
public class KeyLossReportModel {

    public static final int STATUS_PENDING  = 0; // Chờ xử lý
    public static final int STATUS_APPROVED = 1; // Admin xác nhận mất
    public static final int STATUS_REJECTED = 2; // Admin từ chối

    private int    idReport;
    private int    idKey;
    private int    idUser;
    private Timestamp reportTime;   // Thời điểm user BẤM GỬI báo cáo (audit log, không dùng để tính hủy đơn)
    private Timestamp lostTime;     // Thời điểm user CHỌN là lúc bị mất khóa (dùng làm mốc thu hồi key / hủy đơn)
    private String reason;          // Lý do mô tả của người dùng
    private int    status;          // 0 | 1 | 2
    private String adminNote;
    private Timestamp processedAt;  // Thời điểm admin xử lý

    // ── Thông tin JOIN (hiển thị UI) ──────────────────────────
    private String userName;        // fullname từ customer
    private String email;
    private String publicKeyShort;  // 32 ký tự đầu của public key

    // ── Getters / Setters ─────────────────────────────────────

    public int getIdReport() { return idReport; }
    public void setIdReport(int idReport) { this.idReport = idReport; }

    public int getIdKey() { return idKey; }
    public void setIdKey(int idKey) { this.idKey = idKey; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public Timestamp getReportTime() { return reportTime; }
    public void setReportTime(Timestamp reportTime) { this.reportTime = reportTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public Timestamp getProcessedAt() { return processedAt; }
    public void setProcessedAt(Timestamp processedAt) { this.processedAt = processedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPublicKeyShort() { return publicKeyShort; }
    public void setPublicKeyShort(String publicKeyShort) { this.publicKeyShort = publicKeyShort; }

    /** Trả về nhãn trạng thái tiếng Việt */
    public String getStatusLabel() {
        switch (status) {
            case STATUS_APPROVED: return "Đã xác nhận";
            case STATUS_REJECTED: return "Đã từ chối";
            default:              return "Chờ xử lý";
        }
    }

    /** CSS badge class Bootstrap tương ứng */
    public String getStatusBadge() {
        switch (status) {
            case STATUS_APPROVED: return "badge-success";
            case STATUS_REJECTED: return "badge-secondary";
            default:              return "badge-warning";
        }
    }
}
