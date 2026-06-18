package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.model.PublicKeyModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public interface IPublicKeyDao {
    List<PublicKeyModel> getAllKeys();
    boolean revokeKey(int idKey);
    void closeAll(ResultSet rs, PreparedStatement stmt, Connection conn);
}
