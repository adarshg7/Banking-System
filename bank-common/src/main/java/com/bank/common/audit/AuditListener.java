package com.bank.common.audit;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditListener {

    @PrePersist
    public void beforeCreate(Object entity){
        log.debug("About to CREATE entity: {}",entity.getClass().getSimpleName());
    }

    @PostPersist
    public void afterCreate(Object entity){
        log.info("AUDIT [CREATE] entity={} details={}",
                entity.getClass().getSimpleName(),entity);
    }

    @PreUpdate
    public void beforeUpdate(Object entity){
        log.debug("About to UPDATE entity: {}", entity.getClass().getSimpleName());
    }

    @PostUpdate
    public void afterUpdate(Object entity){
        log.info("AUDIT [UPDATE] entity={} details={}", entity.getClass().getSimpleName(),entity);
    }

    @PreRemove
    public void beforeDelete(Object entity){
        log.debug("About to DELETE entity: {}",entity.getClass().getSimpleName());
    }

    @PostRemove
    public void afterDelete(Object entity){
        log.info("AUDIT [DELETE] entity={} details={}",
                entity.getClass().getSimpleName(),entity);
    }
}
