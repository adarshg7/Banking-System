package com.bank.common.audit;

import lombok.extern.slf4j.Slf4j;
import com.bank.common.audit.AuditEvent;
import jakarta.persistence.*;

@Slf4j
public class AuditListener {

    @PostPersist
    public void afterCreate(Object entity) {
        log.info("AUDIT [CREATE] entity={} details={}", entity.getClass().getSimpleName(), entity);
        publishEvent(entity, "CREATE");
    }

    @PostUpdate
    public void afterUpdate(Object entity) {
        log.info("AUDIT [UPDATE] entity={} details={}", entity.getClass().getSimpleName(), entity);
        publishEvent(entity, "UPDATE");
    }

    @PostRemove
    public void afterDelete(Object entity) {
        log.info("AUDIT [DELETE] entity={} details={}", entity.getClass().getSimpleName(), entity);
        publishEvent(entity, "DELETE");
    }

    private void publishEvent(Object entity, String action) {
        try {
            AuditEvent event = new AuditEvent(
                    entity.getClass().getSimpleName(),
                    action,
                    entity.toString()
            );
            SpringContext.getPublisher().publishEvent(event);
        } catch (Exception e) {
            // Deliberately swallow — audit logging must never break the
            // actual business transaction it's observing. A failure here
            // is logged but does not propagate.
            log.warn("Failed to publish audit event for {}: {}", entity.getClass().getSimpleName(), e.getMessage());
        }
    }
}