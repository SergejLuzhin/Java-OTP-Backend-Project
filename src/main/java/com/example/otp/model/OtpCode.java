package com.example.otp.model;

import java.time.LocalDateTime;

public class OtpCode {
    private int id;
    private int userId;
    private String operationId;
    private String code;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public enum Status {
        ACTIVE,
        USED,
        EXPIRED
    }

    public OtpCode(int userId, String operationId, String code, LocalDateTime createdAt, LocalDateTime expiresAt, Status status) {
        this.userId = userId;
        this.operationId = operationId;
        this.code = code;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    // Getters и Setters

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public String getOperationId() { return operationId; }

    public void setOperationId(String operationId) { this.operationId = operationId; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }

    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
