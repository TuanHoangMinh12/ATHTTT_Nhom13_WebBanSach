package vn.edu.hcmuaf.fit.dao.impl;

import vn.edu.hcmuaf.fit.db.JDBCConnector;
import vn.edu.hcmuaf.fit.model.KeyLossReportModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KeyLossReportDao {

    public boolean submitReport(int idKey, int idUser, String reason) {
        Connection conn = null;
        try {
            conn = JDBCConnector.getConnection();
            conn.setAutoCommit(false);

            // Lấy thời điểm báo mất = NOW() — dùng nhất quán trong toàn transaction
            Timestamp reportTime = new Timestamp(System.currentTimeMillis());

            // Bước 1: ghi báo cáo, status=1 (tự động xác nhận luôn)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO key_loss_report (id_key, id_user, reason, status, processed_at) " +
                            "VALUES (?, ?, ?, 1, ?)")) {
                stmt.setInt(1, idKey);
                stmt.setInt(2, idUser);
                stmt.setString(3, reason);
                stmt.setTimestamp(4, reportTime);
                stmt.executeUpdate();
            }

            // Bước 2: thu hồi public_key (status=0, expire=reportTime)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE public_key SET status=0, expire=? WHERE id_key=?")) {
                stmt.setTimestamp(1, reportTime);
                stmt.setInt(2, idKey);
                stmt.executeUpdate();
            }

            // Bước 3: hủy đơn hàng của user tạo SAU thời điểm báo mất
            // (đơn tạo TRƯỚC → giữ nguyên)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE bill SET shipping_info=4 " +
                            "WHERE id_user=? AND create_order_time>? AND shipping_info NOT IN (3,4)")) {
                stmt.setInt(1, idUser);
                stmt.setTimestamp(2, reportTime);
                stmt.executeUpdate();
            }

            // Bước 4: đồng bộ carts.infoShip=4 để UI hiển thị "Đã hủy"
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE carts c " +
                            "JOIN bill b ON b.idCart = c.id " +
                            "SET c.infoShip = 4 " +
                            "WHERE b.id_user=? AND b.create_order_time>? AND c.infoShip NOT IN (3,4)")) {
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

    /** Kiểm tra khóa đã có báo cáo pending chưa (tránh gửi trùng) */
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

    /** Lấy lịch sử báo mất khóa của 1 user — hiển thị trên trang tài khoản */
    public List<KeyLossReportModel> getReportsByUser(int idUser) {
        List<KeyLossReportModel> list = new ArrayList<>();
        String sql =
                "SELECT r.id_report, r.id_key, r.id_user, r.report_time, r.reason, " +
                        "       r.status, r.processed_at, LEFT(pk.public_Key, 32) AS pk_short " +
                        "FROM key_loss_report r " +
                        "JOIN public_key pk ON r.id_key = pk.id_key " +
                        "WHERE r.id_user = ? " +
                        "ORDER BY r.report_time DESC";
        try (Connection conn = JDBCConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    KeyLossReportModel m = new KeyLossReportModel();
                    m.setIdReport(rs.getInt("id_report"));
                    m.setIdKey(rs.getInt("id_key"));
                    m.setIdUser(rs.getInt("id_user"));
                    m.setReportTime(rs.getTimestamp("report_time"));
                    m.setReason(rs.getString("reason"));
                    m.setStatus(rs.getInt("status"));
                    m.setProcessedAt(rs.getTimestamp("processed_at"));
                    m.setPublicKeyShort(rs.getString("pk_short") + "...");
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<KeyLossReportModel> getAllReports() {
        List<KeyLossReportModel> list = new ArrayList<>();
        String sql =
                "SELECT r.id_report, r.id_key, r.id_user, r.report_time, r.reason, " +
                        "       r.status, r.processed_at, LEFT(pk.public_Key, 32) AS pk_short, " +
                        "       CONCAT(c.first_name, ' ', c.last_name) AS fullname, c.email AS email " +
                        "FROM key_loss_report r " +
                        "JOIN public_key pk ON r.id_key = pk.id_key " +
                        "JOIN customer c ON r.id_user = c.id_user " +
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
                m.setProcessedAt(rs.getTimestamp("processed_at"));
                m.setPublicKeyShort(rs.getString("pk_short") + "...");
                m.setUserName(rs.getString("fullname"));
                m.setEmail(rs.getString("email"));
                list.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
