package com.appointment.system.service.impl;

import com.appointment.system.service.interfaces.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            logger.info("Attempting to send email to: {}", to);
            logger.info("Using mail sender: {}", mailSender);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("test@yourapp.com"); // ADD THIS - REQUIRED for Mailtrap
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            logger.info("Sending email...");
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            logger.error("Failed to send email: ", e);
            throw e; // Re-throw to see error in controller
        }
    }
}