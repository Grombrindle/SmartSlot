package com.appointment.system.controller;

import com.appointment.system.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestEmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> testEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message) {
        try {
            System.out.println("=== TESTING EMAIL ===");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);

            emailService.sendSimpleEmail(to, subject, message);

            return ResponseEntity.ok("Email sent successfully! Check Mailtrap inbox.");

        } catch (Exception e) {
            System.err.println("=== EMAIL ERROR ===");
            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("Failed: " + e.getMessage() +
                            "\nFull error: " + e.toString());
        }
    }

    // Add GET method for easier testing
    @GetMapping("/send-email-get")
    public ResponseEntity<String> testEmailGet(
            @RequestParam(defaultValue = "test@example.com") String to,
            @RequestParam(defaultValue = "Test Subject") String subject,
            @RequestParam(defaultValue = "Test message") String message) {
        try {
            emailService.sendSimpleEmail(to, subject, message);
            return ResponseEntity.ok("Email sent via GET! Check Mailtrap.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("GET Failed: " + e.getMessage());
        }
    }
}