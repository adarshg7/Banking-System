package com.bank.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @Column(name = "action", nullable = false)
    private String action; // CREATE, UPDATE, DELETE

    @Column(name = "entity_details", columnDefinition = "TEXT")
    private String entityDetails;

    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}