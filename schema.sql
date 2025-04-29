-- Таблица пользователей
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL,
    telegram_chat_id VARCHAR(64) -- опционально, если будешь использовать Telegram
);

-- Таблица OTP-кодов
CREATE TABLE otp_codes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    operation_id VARCHAR(255) NOT NULL,
    code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'EXPIRED', 'USED'))
);

-- Таблица конфигурации OTP (всегда 1 запись)
CREATE TABLE otp_config (
    code_length INTEGER NOT NULL,
    expiration_minutes INTEGER NOT NULL
);

-- Начальная конфигурация OTP по умолчанию
INSERT INTO otp_config (code_length, expiration_minutes)
VALUES (6, 5);
