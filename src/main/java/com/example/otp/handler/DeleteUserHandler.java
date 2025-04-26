package com.example.otp.handler;

import com.example.otp.dao.OtpDao;
import com.example.otp.dao.UserDao;
import com.example.otp.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;

public class DeleteUserHandler implements HttpHandler {

    private final UserDao userDao;
    private final OtpDao otpDao;

    public DeleteUserHandler(UserDao userDao, OtpDao otpDao) {
        this.userDao = userDao;
        this.otpDao = otpDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("DELETE")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath(); // /admin/users/{username}
        String[] segments = path.split("/");
        if (segments.length != 4) {
            sendResponse(exchange, 400, "Invalid request");
            return;
        }

        String username = segments[3];
        try {
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

        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal server error");
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String msg) throws IOException {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
