package com.warehouse.service;

public interface AuditService {
    void logAction(String action, String entityType, Long entityId, String username, String details);
    void logAction(String action, String entityType, Long entityId, String username);
} 