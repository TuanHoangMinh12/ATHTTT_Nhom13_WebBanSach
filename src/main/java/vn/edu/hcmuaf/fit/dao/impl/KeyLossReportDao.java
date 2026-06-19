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