package com.example.otp.auth;

import com.example.otp.model.User;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminAuthFilter extends Filter {

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        List<String> authHeaders = exchange.getRequestHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            sendForbidden(exchange, "Missing Authorization header");
            return;
        }

        String token = authHeaders.get(0).replace("Bearer", "").trim();

        // 🧪 ВРЕМЕННЫЕ ЛОГИ:
        System.out.println("🧪 Получен токен: " + token);
        User user = AuthTokenManager.getUserByToken(token);
        if (user == null) {
            System.out.println("🔍 Пользователь по токену не найден.");
        } else {
            System.out.println("🔍 Пользователь: " + user.getUsername() + " [" + user.getRole() + "]");
        }

        if (user == null || user.getRole() != User.Role.ADMIN) {
            sendForbidden(exchange, "Access denied: Admin only");
            return;
        }

        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "Only allows access for admin users";
    }

    private void sendForbidden(HttpExchange exchange, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(403, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
