package com.example.otp.handler;

import com.example.otp.auth.AuthTokenManager;
import com.example.otp.model.User;
import com.example.otp.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;

public class LoginHandler implements HttpHandler {

    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
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

        try {
            JSONObject body = new JSONObject(json.toString());
            String username = body.getString("username");
            String password = body.getString("password");

            Optional<User> userOpt = userService.authenticate(username, password);
            if (userOpt.isEmpty()) {
                sendResponse(exchange, 401, "Invalid credentials");
                return;
            }

            String token = AuthTokenManager.generateToken(userOpt.get());

            JSONObject response = new JSONObject();
            response.put("token", token);

            sendResponse(exchange, 200, response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Invalid request");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
