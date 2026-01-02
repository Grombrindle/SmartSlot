package com.appointment.system.service.interfaces;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
}