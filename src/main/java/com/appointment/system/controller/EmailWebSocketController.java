package com.appointment.system.controller;

import com.appointment.system.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class EmailWebSocketController {

    @Autowired
    private EmailService emailService;

    @MessageMapping("/send-email")
    @SendTo("/topic/email-status")
    public EmailResponse sendEmailViaWebSocket(EmailRequest request) {
        try {
            emailService.sendSimpleEmail(
                    request.getToEmail(),
                    request.getSubject(),
                    request.getMessage());

            return new EmailResponse(
                    "Email sent successfully to " + request.getToEmail(),
                    true);
        } catch (Exception e) {
            return new EmailResponse(
                    "Failed to send email: " + e.getMessage(),
                    false);
        }
    }

    // DTO classes using Lombok
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailRequest {
        private String toEmail;
        private String subject;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailResponse {
        private String message;
        private boolean success;
    }
}