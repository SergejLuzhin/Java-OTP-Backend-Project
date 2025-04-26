package com.example.otp.dao;

import com.example.otp.model.OtpCode;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class OtpDao {

    public void save(OtpCode code) throws SQLException {
        String sql = """
            INSERT INTO otp_codes (user_id, operation_id, code, status, created_at, expires_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, code.getUserId());
            stmt.setString(2, code.getOperationId());
            stmt.setString(3, code.getCode());
            stmt.setString(4, code.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(code.getCreatedAt()));
            stmt.setTimestamp(6, Timestamp.valueOf(code.getExpiresAt()));
            stmt.executeUpdate();
        }
    }

    public Optional<OtpCode> findActive(String operationId, int userId) throws SQLException {
        String sql = """
            SELECT * FROM otp_codes
            WHERE user_id = ? AND operation_id = ? AND status = 'ACTIVE' AND expires_at > NOW()
        """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, operationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public void updateStatus(int id, OtpCode.Status status) throws SQLException {
        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    private OtpCode mapRow(ResultSet rs) throws SQLException {
        OtpCode code = new OtpCode(
                rs.getInt("user_id"),
                rs.getString("operation_id"),
                rs.getString("code"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("expires_at").toLocalDateTime(),
                OtpCode.Status.valueOf(rs.getString("status"))
        );
        code.setId(rs.getInt("id"));
        return code;
    }

    public void deleteAllByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM otp_codes WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

}