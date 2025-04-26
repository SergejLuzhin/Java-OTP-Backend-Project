package com.example.otp.handler;

import com.example.otp.dao.OtpConfigDao;
import com.example.otp.model.OtpConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class AdminConfigHandler implements HttpHandler {

    private final OtpConfigDao configDao;

    public AdminConfigHandler(OtpConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                OtpConfig config = configDao.getConfig();
                JSONObject json = new JSONObject();
                json.put("codeLength", config.getCodeLength());
                json.put("expirationMinutes", config.getExpirationMinutes());
                sendResponse(exchange, 200, json.toString());

            } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    body.append(line);
                }
                JSONObject json = new JSONObject(body.toString());

                int length = json.getInt("codeLength");
                int minutes = json.getInt("expirationMinutes");

                configDao.updateConfig(length, minutes);
                sendResponse(exchange, 200, "Configuration updated");
            } else {
                exchange.sendResponseHeaders(405, -1);
            }

        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal server error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}