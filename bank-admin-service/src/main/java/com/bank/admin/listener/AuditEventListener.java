package com.bank.admin.listener;

import com.bank.admin.entity.AuditLog;
import com.bank.admin.repository.AuditLogRepository;
import com.bank.common.audit.AuditEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener {

    private final AuditLogRepository auditLogRepository;

    public AuditEventListener(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * @Async so that persisting the audit log never slows down or risks
     * failing the original business transaction that triggered it. This
     * runs on a separate thread after the triggering transaction has
     * already committed.
     */
    @Async
    @EventListener
    public void handleAuditEvent(AuditEvent event) {
        AuditLog log = new AuditLog();
        log.setEntityName(event.getEntityName());
        log.setAction(event.getAction());
        log.setEntityDetails(event.getEntityDetails());
        log.setPerformedBy(resolveCurrentUser());

        auditLogRepository.save(log);
    }

    private String resolveCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return principal != null ? principal.toString() : "SYSTEM";
        } catch (Exception e) {
            return "SYSTEM";
        }
    }
}