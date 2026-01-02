package com.appointment.system.service;

import java.time.LocalDateTime;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.model.NotificationMessage;

@Service
public class WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    // Send to all subscribed users
    public void sendGlobalNotification(NotificationMessage notification) {
        messagingTemplate.convertAndSend("/topic/global-notifications", notification);
    }
    
    // Send to specific user
    public void sendPrivateNotification(String userId, NotificationMessage notification) {
        messagingTemplate.convertAndSendToUser(
            userId,
            "/queue/private-notifications",
            notification
        );
    }
    
    // Specific appointment notifications
    public void sendAppointmentNotification(Long appointmentId, NotificationMessage notification) {
        messagingTemplate.convertAndSend(
            "/topic/appointments/" + appointmentId,
            notification
        );
    }
    
    // Notify when appointment status changes
    public void notifyAppointmentStatusChange(Long appointmentId, 
                                              AppointmentStatus oldStatus, 
                                              AppointmentStatus newStatus,
                                              Long userId) {
        NotificationMessage notification = new NotificationMessage(
            "APPOINTMENT_STATUS_CHANGED",
            "Appointment status changed from " + oldStatus + " to " + newStatus,
            appointmentId,
            newStatus,
            userId,
            LocalDateTime.now()
        );
        
        // Notify the specific user
        sendPrivateNotification(userId.toString(), notification);
        
        // Also broadcast to admins/staff if needed
        sendGlobalNotification(notification);
    }
}