package com.api.Event_Management_API.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendTestEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("denisot388@exitbit.com");  // change address as needed for testing
        message.setSubject("Spring Boot Email Test");
        message.setText("If you received this, email config is correct!");

        javaMailSender.send(message);
    }
}
