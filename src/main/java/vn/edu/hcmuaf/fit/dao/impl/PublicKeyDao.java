package vn.edu.hcmuaf.fit.dao.impl;

import vn.edu.hcmuaf.fit.dao.IPublicKeyDao;
import vn.edu.hcmuaf.fit.db.JDBCConnector;
import vn.edu.hcmuaf.fit.model.PublicKeyModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PublicKeyDao implements IPublicKeyDao {

    /**
     * Lấy toàn bộ public key kèm tên user (JOIN với customer)
     */
    @Override
    public List<PublicKeyModel> getAllKeys() {
        List<PublicKeyModel> result = new ArrayList<>();
        String sql = "SELECT pk.id_key, pk.id_user, c.firstName, c.lastName, c.email, " +
                "pk.public_Key, pk.status, pk.create_date, pk.expire " +
                "FROM public_key pk " +
                "JOIN customer c ON pk.id_user = c.id_user " +
                "ORDER BY pk.create_date DESC";
        Connection conn = JDBCConnector.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                PublicKeyModel m = new PublicKeyModel();
                m.setIdKey(rs.getInt("id_key"));
                m.setIdUser(rs.getInt("id_user"));
                m.setUserName(rs.getString("firstName") + " " + rs.getString("lastName"));
                m.setEmail(rs.getString("email"));
                m.setPublicKey(rs.getString("public_Key"));
                m.setStatus(rs.getInt("status"));
                m.setCreateDate(rs.getTimestamp("create_date"));
                m.setExpire(rs.getTimestamp("expire"));
                result.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(rs, stmt, conn);
        }
        return result;
    }

    /**
     * Revoke một key: set status = 0
     * status = 1 → Active, status = 0 → Revoked
     */
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