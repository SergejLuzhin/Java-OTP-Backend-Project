package com.example.otp;

import com.example.otp.auth.AdminAuthFilter;
import com.example.otp.dao.OtpConfigDao;
import com.example.otp.dao.OtpDao;
import com.example.otp.dao.UserDao;
import com.example.otp.handler.*;
import com.example.otp.service.OtpService;
import com.example.otp.service.UserService;
import com.example.otp.util.OtpCleanupTask;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws IOException {
        configureLogger();

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // DAO
        UserDao userDao = new UserDao();
        OtpDao otpDao = new OtpDao();
        OtpConfigDao configDao = new OtpConfigDao();

        // Services
        UserService userService = new UserService(userDao);
        OtpService otpService = new OtpService(otpDao, configDao);

        // Handlers
        server.createContext("/register", new RegisterHandler(userService));
        server.createContext("/login", new LoginHandler(userService));
        server.createContext("/generate", new GenerateOtpHandler(otpService, userDao));
        server.createContext("/verify", new VerifyOtpHandler(otpService, userDao));

        // Admin-only endpoints
        server.createContext("/admin/config", new AdminConfigHandler(configDao))
                .getFilters().add(new AdminAuthFilter());

        server.createContext("/admin/users", new AdminUsersHandler(userDao, otpDao))
                .getFilters().add(new AdminAuthFilter());

        // Cleanup expired OTP codes
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new OtpCleanupTask(), 0, 1, TimeUnit.MINUTES);

        server.setExecutor(null);
        server.start();
        logger.info("🚀 Server started on http://localhost:" + port);
    }

    private static void configureLogger() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        rootLogger.addHandler(handler);
    }
}
