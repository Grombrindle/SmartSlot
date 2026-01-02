package com.appointment.system.service.interfaces;

import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.model.NotificationMessage;

public interface WebSocketNotificationService {
    void sendGlobalNotification(NotificationMessage notification);
    
    void sendPrivateNotification(String userId, NotificationMessage notification);
    
    void sendAppointmentNotification(Long appointmentId, NotificationMessage notification);
    
    void notifyAppointmentStatusChange(Long appointmentId, 
                                       AppointmentStatus oldStatus, 
                                       AppointmentStatus newStatus,
                                       Long userId);
}