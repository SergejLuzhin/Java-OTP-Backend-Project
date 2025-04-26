package com.example.otp.service;

import com.example.otp.dao.UserDao;
import com.example.otp.model.User;

import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> authenticate(String username, String password) throws SQLException {
        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();

        String hash = hashPassword(password);
        if (userOpt.get().getPasswordHash().equals(hash)) {
            return userOpt;
        }

        return Optional.empty();
    }

    public boolean register(String username, String password, User.Role role) throws SQLException {
        if (role == User.Role.ADMIN && userDao.adminExists()) {
            return false;
        }

        Optional<User> existing = userDao.findByUsername(username);
        if (existing.isPresent()) {
            return false;
        }

        String hash = hashPassword(password);
        User user = new User(0, username, hash, role);
        return userDao.createUser(user);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка хеширования пароля", e);
        }
    }
}
