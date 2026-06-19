package vn.edu.hcmuaf.fit.dao.impl;

import vn.edu.hcmuaf.fit.dao.IPublicKeyDao;
import vn.edu.hcmuaf.fit.db.JDBCConnector;
import vn.edu.hcmuaf.fit.model.PublicKeyModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PublicKeyDao implements IPublicKeyDao {

    @Override
    public List<PublicKeyModel> getAllKeys() {
        List<PublicKeyModel> list = new ArrayList<>();
        // JOIN chính xác từ bảng customer để lấy Họ tên và Email hiển thị lên giao diện
        String sql = "SELECT pk.id_key, pk.id_user, CONCAT(c.first_name, ' ', c.last_name) AS fullname, " +
                "c.email, pk.public_Key, pk.status, pk.create_date, pk.expire " +
                "FROM public_key pk " +
                "JOIN customer c ON pk.id_user = c.id_user " +
                "ORDER BY pk.create_date DESC";

        try (Connection conn = JDBCConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PublicKeyModel model = new PublicKeyModel();
                model.setIdKey(rs.getInt("id_key"));
                model.setIdUser(rs.getInt("id_user"));
                model.setUserName(rs.getString("fullname"));
                model.setEmail(rs.getString("email"));
                model.setPublicKey(rs.getString("public_Key"));
                model.setStatus(rs.getInt("status"));
                model.setCreateDate(rs.getTimestamp("create_date"));
                model.setExpire(rs.getTimestamp("expire"));
                list.add(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean revokeKey(int idKey) {
        String sql = "UPDATE public_key SET status = 0 WHERE id_key = ?";
        Connection conn = JDBCConnector.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idKey);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeAll(null, stmt, conn);
        }
    }

    @Override
    public void closeAll(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ignored) {
        }
    }
}