package vn.edu.hcmuaf.fit.dao.impl;

import vn.edu.hcmuaf.fit.db.JDBCConnector;
import vn.edu.hcmuaf.fit.model.KeyLossReportModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KeyLossReportDao {

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

    public List<KeyLossReportModel> getPendingReports() {
        return queryReports("WHERE r.status = 0");
    }

    public List<KeyLossReportModel> getAllReports() {
        return queryReports(null);
    }

    public boolean approveReport(int idReport, String adminNote) {
        Connection conn = null;
        try {
            conn = JDBCConnector.getConnection();
            conn.setAutoCommit(false);

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

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE key_loss_report SET status=1, admin_note=?, processed_at=NOW() WHERE id_report=?")) {
                stmt.setString(1, adminNote);
                stmt.setInt(2, idReport);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE public_key SET status=0, expire=? WHERE id_key=?")) {
                stmt.setTimestamp(1, reportTime);
                stmt.setInt(2, idKey);
                stmt.executeUpdate();
            }


            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE bill SET shipping_info=4 " +
                            "WHERE id_user=? AND create_order_time>? AND shipping_info NOT IN (3,4)")) {
                stmt.setInt(1, idUser);
                stmt.setTimestamp(2, reportTime);
                stmt.executeUpdate();
            }

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
    /**
     * Admin từ chối báo cáo
     */
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

    /**
     * Kiểm tra khóa đã có báo cáo đang pending chưa (tránh gửi trùng)
     */
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

    /**
     * Đếm số báo cáo pending — dùng cho badge trên menu admin
     */
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
}