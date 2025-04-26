package com.example.otp.handler;

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

public class GetUsersHandler implements HttpHandler {

    private final UserDao userDao;

    public GetUsersHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            List<User> users = userDao.findAllNonAdmins();
            JSONArray array = new JSONArray();

            for (User user : users) {
                JSONObject json = new JSONObject();
                json.put("id", user.getId());
                json.put("username", user.getUsername());
                array.put(json);
            }

            sendResponse(exchange, 200, array.toString());

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
