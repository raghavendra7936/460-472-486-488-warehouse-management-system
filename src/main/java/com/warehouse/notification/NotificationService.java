package com.warehouse.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final List<Notification> notifications;

    @Autowired
    public NotificationService(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void notifyAll(String message, String recipient) {
        notifications.forEach(notification -> notification.send(message, recipient));
    }
} 