package com.appointment.system.service.impl;

import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.model.NotificationMessage;
import com.appointment.system.service.interfaces.WebSocketNotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WebSocketNotificationServiceImpl implements WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketNotificationServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @Override
    public void sendGlobalNotification(NotificationMessage notification) {
        messagingTemplate.convertAndSend("/topic/global-notifications", notification);
    }
    
    @Override
    public void sendPrivateNotification(String userId, NotificationMessage notification) {
        messagingTemplate.convertAndSendToUser(
            userId,
            "/queue/private-notifications",
            notification
        );
    }
    
    @Override
    public void sendAppointmentNotification(Long appointmentId, NotificationMessage notification) {
        messagingTemplate.convertAndSend(
            "/topic/appointments/" + appointmentId,
            notification
        );
    }
    
    @Override
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