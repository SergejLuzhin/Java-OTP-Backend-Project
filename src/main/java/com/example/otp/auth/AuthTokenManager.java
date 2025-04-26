package com.example.otp.auth;

import com.example.otp.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthTokenManager {
    private static final Map<String, User> tokenStore = new ConcurrentHashMap<>();

    public static String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user);
        return token;
    }

    public static User getUserByToken(String token) {
        return tokenStore.get(token);
    }

    public static boolean isAdmin(String token) {
        User user = tokenStore.get(token);
        return user != null && user.getRole() == User.Role.ADMIN;
    }
}
