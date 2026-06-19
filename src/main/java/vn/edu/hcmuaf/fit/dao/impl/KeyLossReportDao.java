package vn.edu.hcmuaf.fit.dao.impl;

import vn.edu.hcmuaf.fit.db.JDBCConnector;
import vn.edu.hcmuaf.fit.model.KeyLossReportModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KeyLossReportDao {

    /** Người dùng gửi báo cáo mất khóa */
    public boolean submitReport(int idKey, int idUser, String reason) {
        String sql = "INSERT INTO key_loss_report (id_key, id_user, reason, status) VALUES (?, ?, ?, 0)";
        try (Connection conn = JDBCConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idKey);
            stmt.setInt(2, idUser);
            stmt.setString(3, reason);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Admin: danh sách chờ xử lý */
    public List<KeyLossReportModel> getPendingReports() {
        return queryReports("WHERE r.status = 0");
    }

    /** Admin: toàn bộ lịch sử */
    public List<KeyLossReportModel> getAllReports() {
        return queryReports(null);
    }

    /**
     * Admin XÁC NHẬN mất khóa — transaction 4 bước:
     * 1. Lấy id_key + report_time
     * 2. Cập nhật báo cáo → status=1
     * 3. Thu hồi public_key (expire = report_time)
     * 4. Đơn hàng dùng khóa đó TẠO SAU report_time → shipping_info=4 (hủy), carts.infoShip=4
     */
    public boolean approveReport(int idReport, String adminNote) {
        Connection conn = null;
        try {
            conn = JDBCConnector.getConnection();
            conn.setAutoCommit(false);

            // Bước 1: lấy thông tin báo cáo (id_key, id_user, report_time)
            int idKey;
            int idUser;
            Timestamp reportTime;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id_key, id_user, report_time FROM key_loss_report WHERE id_report = ?")) {
                stmt.setInt(1, idReport);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) { conn.rollback(); return false; }
                idKey      = rs.getInt("id_key");
                idUser     = rs.getInt("id_user");
                reportTime = rs.getTimestamp("report_time");
            }

            // Bước 2: cập nhật báo cáo
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE key_loss_report SET status=1, admin_note=?, processed_at=NOW() WHERE id_report=?")) {
                stmt.setString(1, adminNote);
                stmt.setInt(2, idReport);
                stmt.executeUpdate();
            }

            // Bước 3: thu hồi khóa (expire = thời điểm báo mất, không phải NOW())
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE public_key SET status=0, expire=? WHERE id_key=?")) {
                stmt.setTimestamp(1, reportTime);
                stmt.setInt(2, idKey);
                stmt.executeUpdate();
            }

            // Bước 4a: bill.shipping_info = 4 (hủy)
            // Điều kiện: đúng user + đơn tạo SAU thời điểm báo mất + chưa hoàn thành/hủy
            // Dùng id_user thay id_key vì bill.id_key có thể NULL (đơn cũ)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE bill SET shipping_info=4 " +
                            "WHERE id_user=? AND create_order_time>? AND shipping_info NOT IN (3,4)")) {
                stmt.setInt(1, idUser);
                stmt.setTimestamp(2, reportTime);
                stmt.executeUpdate();
            }

            // Bước 4b: đồng bộ carts.infoShip = 4
            // UI đọc từ carts.infoShip — phải UPDATE cả 2 bảng mới hiện lên giao diện
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE carts c " +
                            "JOIN bill b ON b.idCart = c.id " +
                            "SET c.infoShip = 4 " +
                            "WHERE b.id_user = ? AND b.create_order_time > ? AND c.infoShip NOT IN (3,4)")) {
                stmt.setInt(1, idUser);
                stmt.setTimestamp(2, reportTime);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

    /** Admin từ chối báo cáo */
    public boolean rejectReport(int idReport, String adminNote) {
        String sql = "UPDATE key_loss_report SET status=2, admin_note=?, processed_at=NOW() WHERE id_report=?";
        try (Connection conn = JDBCConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, adminNote);
            stmt.setInt(2, idReport);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Kiểm tra khóa đã có báo cáo đang pending chưa (tránh gửi trùng) */
    public boolean hasPendingReport(int idKey) {
        String sql = "SELECT COUNT(*) FROM key_loss_report WHERE id_key=? AND status=0";
        try (Connection conn = JDBCConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idKey);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Đếm số báo cáo pending — dùng cho badge trên menu admin */
    public int countPending() {
        String sql = "SELECT COUNT(*) FROM key_loss_report WHERE status=0";
        try (Connection conn = JDBCConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private List<KeyLossReportModel> queryReports(String whereClause) {
        List<KeyLossReportModel> list = new ArrayList<>();
        String sql =
                "SELECT r.id_report, r.id_key, r.id_user, r.report_time, r.reason, " +
                        "       r.status, r.admin_note, r.processed_at, " +
                        "       CONCAT(c.first_name,' ',c.last_name) AS user_name, " +
                        "       c.email, LEFT(pk.public_Key, 32) AS pk_short " +
                        "FROM key_loss_report r " +
                        "JOIN customer c   ON r.id_user = c.id_user " +
                        "JOIN public_key pk ON r.id_key  = pk.id_key " +
                        (whereClause != null ? whereClause + " " : "") +
                        "ORDER BY r.report_time DESC";
        try (Connection conn = JDBCConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                KeyLossReportModel m = new KeyLossReportModel();
                m.setIdReport(rs.getInt("id_report"));
                m.setIdKey(rs.getInt("id_key"));
                m.setIdUser(rs.getInt("id_user"));
                m.setReportTime(rs.getTimestamp("report_time"));
                m.setReason(rs.getString("reason"));
                m.setStatus(rs.getInt("status"));
                m.setAdminNote(rs.getString("admin_note"));
                m.setProcessedAt(rs.getTimestamp("processed_at"));
                m.setUserName(rs.getString("user_name"));
                m.setEmail(rs.getString("email"));
                m.setPublicKeyShort(rs.getString("pk_short") + "...");
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
