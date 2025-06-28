package com.api.Event_Management_API.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendVerificationEmail(String to, String token) {
        String subject = "Xác minh tài khoản của bạn";
        String verificationUrl = "http://localhost:5555/api/auth/verify/" + token;
        String message = "Cảm ơn bạn đã đăng ký!\n\nVui lòng xác minh tài khoản của bạn bằng cách nhấp vào liên kết dưới đây:\n" + verificationUrl + "\n\nLiên kết này sẽ hết hạn sau 3 ngày.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        javaMailSender.send(email);
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = "http://your-frontend-domain.com/reset_password/" + token;

        String subject = "Reset Your Password";
        String message = "Click the link below to reset your password:\n" + resetLink +
                "\n\nThis link will expire in 30 minutes.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        javaMailSender.send(email);
    }
}
