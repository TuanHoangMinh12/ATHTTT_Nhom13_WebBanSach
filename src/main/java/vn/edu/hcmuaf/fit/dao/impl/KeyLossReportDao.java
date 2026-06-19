package vn.edu.hcmuaf.fit.dao.impl;

import vn.edu.hcmuaf.fit.dao.IKeyLossReportDao;
import vn.edu.hcmuaf.fit.db.JDBCConnector;
import vn.edu.hcmuaf.fit.model.KeyLossReportModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KeyLossReportDao implements IKeyLossReportDao {

    // ─────────────────────────────────────────────────────────────────────────
    // Người dùng gửi báo cáo mất khóa
    // report_time = NOW() tự động (audit: lúc bấm gửi)
    // lost_time   = do user chọn (lúc thực sự bị mất khóa) — dùng để tính hủy đơn
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public boolean submitReport(int idKey, int idUser, String reason, Timestamp lostTime) {
        String sql = "INSERT INTO key_loss_report (id_key, id_user, reason, lost_time, status) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = JDBCConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idKey);
            stmt.setInt(2, idUser);
            stmt.setString(3, reason);
            if (lostTime != null) {
                stmt.setTimestamp(4, lostTime);
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin: danh sách chờ xử lý
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public List<KeyLossReportModel> getPendingReports() {
        return queryReports("WHERE r.status = 0");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin: toàn bộ lịch sử
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public List<KeyLossReportModel> getAllReports() {
        return queryReports(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin XÁC NHẬN mất khóa — 3 bước trong một transaction
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public boolean approveReport(int idReport, String adminNote) {
        Connection conn = null;
        try {
            conn = JDBCConnector.getConnection();
            conn.setAutoCommit(false);

            // Bước 1: Lấy thông tin báo cáo (id_key, lost_time, report_time)
            int idKey;
            Timestamp cutoffTime;
            String selectReport = "SELECT id_key, lost_time, report_time FROM key_loss_report WHERE id_report = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectReport)) {
                stmt.setInt(1, idReport);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                idKey = rs.getInt("id_key");

                // Ưu tiên lost_time (thời điểm user chọn là lúc bị mất).
                // Fallback report_time cho các report cũ tạo trước khi có cột lost_time.
                Timestamp lostTime   = rs.getTimestamp("lost_time");
                Timestamp reportTime = rs.getTimestamp("report_time");
                cutoffTime = (lostTime != null) ? lostTime : reportTime;
            }

            // Bước 2: Cập nhật trạng thái báo cáo → đã xác nhận
            String updateReport = "UPDATE key_loss_report SET status = 1, admin_note = ?, processed_at = NOW() WHERE id_report = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateReport)) {
                stmt.setString(1, adminNote);
                stmt.setInt(2, idReport);
                stmt.executeUpdate();
            }

            // Bước 3: Thu hồi public key — expire = cutoffTime (không phải NOW()
            //         để đơn hàng trước thời điểm mất khóa thực sự vẫn hợp lệ)
            String revokeKey = "UPDATE public_key SET status = 0, expire = ? WHERE id_key = ?";
            try (PreparedStatement stmt = conn.prepareStatement(revokeKey)) {
                stmt.setTimestamp(1, cutoffTime);
                stmt.setInt(2, idKey);
                stmt.executeUpdate();
            }

            // Bước 4: Đơn hàng dùng khóa này mà tạo SAU thời điểm mất khóa → hủy (status = 4)
            //         Đơn hàng tạo TRƯỚC hoặc BẰNG cutoffTime → giữ nguyên (vẫn hợp lệ)
            //         Chỉ hủy đơn chưa hoàn thành (shipping_info != 3) và chưa hủy (!=4)
            String cancelOrders =
                    "UPDATE bill SET shipping_info = 4 " +
                            "WHERE id_key = ? " +
                            "  AND create_order_time > ? " +
                            "  AND shipping_info NOT IN (3, 4)";
            try (PreparedStatement stmt = conn.prepareStatement(cancelOrders)) {
                stmt.setInt(1, idKey);
                stmt.setTimestamp(2, cutoffTime);
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

    // ─────────────────────────────────────────────────────────────────────────
    // Admin TỪ CHỐI báo cáo
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public boolean rejectReport(int idReport, String adminNote) {
        String sql = "UPDATE key_loss_report SET status = 2, admin_note = ?, processed_at = NOW() WHERE id_report = ?";
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

    // ─────────────────────────────────────────────────────────────────────────
    // Kiểm tra user đã có báo cáo pending chưa (tránh gửi trùng)
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public boolean hasPendingReport(int idKey) {
        String sql = "SELECT COUNT(*) FROM key_loss_report WHERE id_key = ? AND status = 0";
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

    // ─────────────────────────────────────────────────────────────────────────
    // Helper: query danh sách báo cáo có JOIN customer và public_key
    // ─────────────────────────────────────────────────────────────────────────
    private List<KeyLossReportModel> queryReports(String whereClause) {
        List<KeyLossReportModel> list = new ArrayList<>();
        String sql =
                "SELECT r.id_report, r.id_key, r.id_user, r.report_time, r.lost_time, r.reason, " +
                        "       r.status, r.admin_note, r.processed_at, " +
                        "       CONCAT(c.first_name, ' ', c.last_name) AS user_name, " +
                        "       c.email, " +
                        "       LEFT(pk.public_Key, 32) AS public_key_short " +
                        "FROM key_loss_report r " +
                        "JOIN customer   c  ON r.id_user = c.id_user " +
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
                m.setLostTime(rs.getTimestamp("lost_time"));
                m.setReason(rs.getString("reason"));
                m.setStatus(rs.getInt("status"));
                m.setAdminNote(rs.getString("admin_note"));
                m.setProcessedAt(rs.getTimestamp("processed_at"));
                m.setUserName(rs.getString("user_name"));
                m.setEmail(rs.getString("email"));
                m.setPublicKeyShort(rs.getString("public_key_short") + "...");
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
