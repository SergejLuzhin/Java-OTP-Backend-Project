package com.example.otp.dao;

import com.example.otp.model.OtpConfig;

import java.sql.*;

public class OtpConfigDao {

    public OtpConfig getConfig() throws SQLException {
        String sql = "SELECT * FROM otp_config LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new OtpConfig(rs.getInt("code_length"), rs.getInt("expiration_minutes"));
            } else {
                return new OtpConfig(6, 5); // дефолт
            }
        }
    }

    public void updateConfig(int codeLength, int expirationMinutes) throws SQLException {
        String deleteSql = "DELETE FROM otp_config";
        String insertSql = "INSERT INTO otp_config (code_length, expiration_minutes) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                deleteStmt.executeUpdate();
                insertStmt.setInt(1, codeLength);
                insertStmt.setInt(2, expirationMinutes);
                insertStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
