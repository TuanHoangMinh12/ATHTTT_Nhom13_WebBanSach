package vn.edu.hcmuaf.fit.model;

import java.sql.Timestamp;

/**
 * Model đại diện cho yêu cầu báo mất khóa private key của người dùng.
 *
 * LƯU Ý (cập nhật luồng tự động xử lý):
 * Từ khi áp dụng luồng "tự động xử lý ngay khi user báo mất" (xem
 * KeyLossReportDao#submitReport), mọi báo cáo được ghi với status = 1
 * ngay lập tức — không còn trạng thái chờ admin xác nhận (PENDING) hay
 * admin từ chối (REJECTED) trong thực tế nữa. Các hằng số STATUS_PENDING /
 * STATUS_REJECTED được giữ lại để tương thích dữ liệu cũ (nếu còn record
 * cũ trong DB từ trước khi đổi luồng) và để trang Admin lịch sử hiển thị
 * đúng nhãn cho các record đó. Chỉ có DUY NHẤT một mốc thời gian
 * (reportTime) — là lúc người dùng bấm nút báo mất — được dùng làm mốc
 * để thu hồi key và hủy đơn hàng tạo sau thời điểm đó.
 */
public class KeyLossReportModel {

    public static final int STATUS_PENDING  = 0; // (Luồng cũ) Chờ admin xử lý
    public static final int STATUS_APPROVED = 1; // Đã xử lý — khóa đã bị vô hiệu hóa
    public static final int STATUS_REJECTED = 2; // (Luồng cũ) Admin từ chối

    private int    idReport;
    private int    idKey;
    private int    idUser;
    private Timestamp reportTime;   // Mốc duy nhất: lúc user bấm "Báo Mất Khóa" — dùng để thu hồi key + hủy đơn hàng
    private String reason;          // Lý do mô tả của người dùng
    private int    status;          // 0 | 1 | 2
    private String adminNote;
    private Timestamp processedAt;  // Thời điểm xử lý (= reportTime vì xử lý ngay)

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
            case STATUS_APPROVED: return "Đã xử lý";
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
