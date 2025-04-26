package com.example.otp.handler;

import com.example.otp.dao.OtpDao;
import com.example.otp.dao.UserDao;
import com.example.otp.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminUsersHandler implements HttpHandler {

    private final UserDao userDao;
    private final OtpDao otpDao;

    public AdminUsersHandler(UserDao userDao, OtpDao otpDao) {
        this.userDao = userDao;
        this.otpDao = otpDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                // Получить всех пользователей (без админов)
                List<User> users = userDao.findAllNonAdmins();
                JSONArray array = new JSONArray();
                for (User user : users) {
                    JSONObject json = new JSONObject();
                    json.put("id", user.getId());
                    json.put("username", user.getUsername());
                    array.put(json);
                }
                sendResponse(exchange, 200, array.toString());

            } else if (exchange.getRequestMethod().equalsIgnoreCase("DELETE")) {
                // Удалить пользователя
                String path = exchange.getRequestURI().getPath(); // /admin/users/{username}
                String[] segments = path.split("/");
                if (segments.length != 4) {
                    sendResponse(exchange, 400, "Invalid request");
                    return;
                }
                String username = segments[3];

                Optional<User> userOpt = userDao.findByUsername(username);
                if (userOpt.isEmpty()) {
                    sendResponse(exchange, 404, "User not found");
                    return;
                }

                if (userOpt.get().getRole() == User.Role.ADMIN) {
                    sendResponse(exchange, 403, "Cannot delete admin");
                    return;
                }

                otpDao.deleteAllByUserId(userOpt.get().getId());
                boolean deleted = userDao.deleteByUsername(username);
                if (deleted) {
                    sendResponse(exchange, 200, "User deleted");
                } else {
                    sendResponse(exchange, 500, "Deletion failed");
                }

            } else {
                exchange.sendResponseHeaders(405, -1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal server error");
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
