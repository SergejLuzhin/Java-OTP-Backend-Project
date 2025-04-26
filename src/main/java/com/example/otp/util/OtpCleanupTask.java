package com.example.otp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.example.otp.dao.DatabaseConnection;

public class OtpCleanupTask implements Runnable {
    private static final Logger logger = Logger.getLogger(OtpCleanupTask.class.getName());

    @Override
    public void run() {
        try {
            int expired = markExpiredOtps();
            if (expired > 0) {
                logger.info("⏳ Обновлено просроченных OTP: " + expired);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка при обновлении истекших OTP: " + e.getMessage());
        }
    }

    private int markExpiredOtps() throws SQLException {
        String sql = """
            UPDATE otp_codes
            SET status = 'EXPIRED'
            WHERE status = 'ACTIVE' AND expires_at < NOW()
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate();
        }
    }
}