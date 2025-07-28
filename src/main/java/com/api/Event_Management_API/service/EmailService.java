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
        String verificationUrl = "http://localhost:10000/api/auth/verify/" + token;
        String message = "Cảm ơn bạn đã đăng ký!\n\nVui lòng xác minh tài khoản của bạn bằng cách nhấp vào liên kết dưới đây:\n" + verificationUrl + "\n\nLiên kết này sẽ hết hạn sau 3 ngày.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        javaMailSender.send(email);
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = "http://localhost:10000/reset-password/" + token;

        String subject = "Reset Your Password";
        String message = "Click the link below to reset your password:\n" + resetLink +
                "\n\nThis link will expire in 30 minutes.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        javaMailSender.send(email);
    }

    public void sendTicketConfirmationEmail(String to, String tenKhachHang) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Support Request Received");
        message.setText("Hi " + tenKhachHang + ",\n\n"
                + "We have received your support request. Our staff will respond shortly.\n\n"
                + "Thank you.");

        javaMailSender.send(message);
    }

    public void sendResponseTicketEmail(String to, String tenKhachHang, String response, String tenNhanVien) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Ticket reply");
        message.setText("Chào " + tenKhachHang + "\n\n" + response + "\n\n" + tenNhanVien);

        javaMailSender.send(message);
    }
}
