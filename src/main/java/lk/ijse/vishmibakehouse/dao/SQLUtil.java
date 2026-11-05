package lk.ijse.vishmibakehouse.dao;

import lk.ijse.vishmibakehouse.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtil {
    private static PreparedStatement getPreparedStatement(String sql, Object... params) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            pstm.setObject(i + 1, params[i]);
        }
        return pstm;
    }

    public static boolean executeUpdate(String sql, Object... params) throws SQLException {
        return getPreparedStatement(sql, params).executeUpdate() > 0;
    }

    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        return getPreparedStatement(sql, params).executeQuery();
    }

    public static long executeInsertReturnGeneratedId(String sql, Object... params) throws SQLException {
        PreparedStatement pstm = getPreparedStatement(sql, params);
        pstm.executeUpdate();
        ResultSet rs = pstm.getGeneratedKeys();
        if (rs.next()) {
            return rs.getLong(1);
        }
        return -1;
    }
}
