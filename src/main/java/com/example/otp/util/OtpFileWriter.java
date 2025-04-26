package com.example.otp.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class OtpFileWriter {

    public static void saveToFile(String username, String operationId, String code) {
        String filename = "otp_" + username + "_" + operationId + ".txt";
        Path filePath = Path.of(System.getProperty("user.dir"), filename);

        String content = "OTP для пользователя: " + username + "\n" +
                "Операция: " + operationId + "\n" +
                "Код: " + code + "\n" +
                "Время: " + LocalDateTime.now() + "\n";

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(content);
            System.out.println("OTP-сохранён в файл: " + filename);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении OTP в файл: " + e.getMessage());
        }
    }
}
