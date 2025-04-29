package com.example.otp.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailSender {

    private static final String FROM = "luzhins764@gmail.com";
    private static final String PASSWORD = "rdzx gtdt mlul olwd";

    public static void sendOtp(String toEmail, String otp) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Ваш OTP-код");
        message.setText("Здравствуйте!\n\nВаш OTP-код: " + otp + "\n\nС уважением,\nOTP-сервис");

        Transport.send(message);
        System.out.println("Отправлен OTP-код на email: " + toEmail);
    }
}
