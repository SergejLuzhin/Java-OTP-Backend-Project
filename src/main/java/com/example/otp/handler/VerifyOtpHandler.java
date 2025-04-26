package com.example.otp.handler;

import com.example.otp.dao.UserDao;
import com.example.otp.model.User;
import com.example.otp.service.OtpService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;

public class VerifyOtpHandler implements HttpHandler {

    private final OtpService otpService;
    private final UserDao userDao;

    public VerifyOtpHandler(OtpService otpService, UserDao userDao) {
        this.otpService = otpService;
        this.userDao = userDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            json.append(line);
        }

        JSONObject body = new JSONObject(json.toString());
        String username = body.getString("username");
        String operationId = body.getString("operationId");
        String code = body.getString("code");

        try {
            Optional<User> userOpt = userDao.findByUsername(username);
            if (userOpt.isEmpty()) {
                sendResponse(exchange, 404, "User not found");
                return;
            }

            int userId = userOpt.get().getId();
            boolean valid = otpService.verifyOtp(userId, operationId, code);

            JSONObject response = new JSONObject();
            response.put("verified", valid);
            sendResponse(exchange, valid ? 200 : 400, response.toString());

        } catch (SQLException e) {
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
