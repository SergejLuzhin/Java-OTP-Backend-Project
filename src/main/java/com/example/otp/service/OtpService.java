package com.example.otp.service;

import com.example.otp.dao.OtpConfigDao;
import com.example.otp.dao.OtpDao;
import com.example.otp.model.OtpCode;
import com.example.otp.model.OtpConfig;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

public class OtpService {
    private final OtpDao otpDao;
    private final OtpConfigDao configDao;
    private final Random random = new Random();

    public OtpService(OtpDao otpDao, OtpConfigDao configDao) {
        this.otpDao = otpDao;
        this.configDao = configDao;
    }

    public String generateOtp(int userId, String operationId) throws SQLException {
        OtpConfig config = configDao.getConfig();

        String code = generateCode(config.getCodeLength());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(config.getExpirationMinutes());

        OtpCode otp = new OtpCode(userId, operationId, code, now, expiresAt, OtpCode.Status.ACTIVE);
        otpDao.save(otp);
        return code;
    }

    public boolean verifyOtp(int userId, String operationId, String inputCode) throws SQLException {
        Optional<OtpCode> otpOpt = otpDao.findActive(operationId, userId);
        if (otpOpt.isEmpty()) return false;

        OtpCode otp = otpOpt.get();
        if (otp.getCode().equals(inputCode)) {
            otpDao.updateStatus(otp.getId(), OtpCode.Status.USED);
            return true;
        }
        return false;
    }

    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
