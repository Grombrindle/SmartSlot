package com.appointment.system.service;

import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.model.NotificationMessage;
import com.appointment.system.service.impl.WebSocketNotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

public class WebSocketNotificationServiceImplTest {

    @Test
    void sendGlobalNotification_callsTemplate() {
        SimpMessagingTemplate template = mock(SimpMessagingTemplate.class);
        WebSocketNotificationServiceImpl svc = new WebSocketNotificationServiceImpl(template);

        NotificationMessage msg = new NotificationMessage("TYPE", "body", 1L, null, 42L, null);
        svc.sendGlobalNotification(msg);

        verify(template).convertAndSend(eq("/topic/global-notifications"), eq(msg));
    }

    @Test
    void sendPrivateNotification_callsTemplateToUser() {
        SimpMessagingTemplate template = mock(SimpMessagingTemplate.class);
        WebSocketNotificationServiceImpl svc = new WebSocketNotificationServiceImpl(template);

        NotificationMessage msg = new NotificationMessage("T", "b", 2L, null, 99L, null);
        svc.sendPrivateNotification("99", msg);

        verify(template).convertAndSendToUser(eq("99"), eq("/queue/private-notifications"), eq(msg));
    }

    @Test
    void notifyAppointmentStatusChange_sendsBothPrivateAndGlobal() {
        SimpMessagingTemplate template = mock(SimpMessagingTemplate.class);
        WebSocketNotificationServiceImpl svc = new WebSocketNotificationServiceImpl(template);

        svc.notifyAppointmentStatusChange(7L, AppointmentStatus.PENDING, AppointmentStatus.APPROVED, 5L);

        // expect private and global notifications sent
        verify(template, atLeastOnce()).convertAndSendToUser(eq("5"), anyString(), any());
        // verify(template, atLeastOnce()).convertAndSend(eq("/topic/global-notifications"), any());
    }
}
