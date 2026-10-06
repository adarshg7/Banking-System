package com.bank.common.audit;

public class AuditEvent {

    private final String entityName;
    private final String action;
    private final String entityDetails;

    public AuditEvent(String entityName, String action, String entityDetails) {
        this.entityName = entityName;
        this.action = action;
        this.entityDetails = entityDetails;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getAction() {
        return action;
    }

    public String getEntityDetails() {
        return entityDetails;
    }
}