package com.warehouse.notification;

import org.springframework.stereotype.Component;

@Component
public class EmailNotification implements Notification {
    @Override
    public void send(String message, String recipient) {
        // In a real application, this would send an email
        System.out.println("Sending email to " + recipient + ": " + message);
    }
} 