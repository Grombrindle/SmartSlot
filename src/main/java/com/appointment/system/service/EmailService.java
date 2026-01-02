package com.appointment.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // @Bean
    // public CommandLineRunner testEmail(JavaMailSender mailSender) {
    //     return args -> {
    //         System.out.println("Testing email send...");
    //         SimpleMailMessage message = new SimpleMailMessage();
    //         message.setFrom("test@yourapp.com");
    //         message.setTo("recipient@example.com");
    //         message.setSubject("Test from Spring Boot");
    //         message.setText("If you get this, Mailtrap config is correct.");
    //         mailSender.send(message);
    //         System.out.println("Test email sent. Check your Mailtrap inbox.");
    //     };
    // }

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