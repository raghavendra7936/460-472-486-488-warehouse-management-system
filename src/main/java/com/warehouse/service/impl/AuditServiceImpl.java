package com.warehouse.service.impl;

import com.warehouse.model.AuditLog;
import com.warehouse.repository.AuditLogRepository;
import com.warehouse.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public void logAction(String action, String entityType, Long entityId, String username, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setUsername(username);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }

    @Override
    public void logAction(String action, String entityType, Long entityId, String username) {
        logAction(action, entityType, entityId, username, null);
    }
} 