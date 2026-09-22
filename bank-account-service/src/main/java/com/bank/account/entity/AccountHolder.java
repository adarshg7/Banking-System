package com.bank.account.entity;

import com.bank.account.enums.HolderRelation;
import com.bank.common.audit.Auditable;
import com.bank.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_holders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountHolder extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "holder_relation",nullable = false)
    private HolderRelation holderRelation;

    @Column(name = "is_primary_holder", nullable = false)
    @Builder.Default
    private boolean isPrimaryHolder  = false;

    @Column(name = "can_operate", nullable = false)
    @Builder.Default
    private boolean canOperate = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column(name = "added_date", nullable = false)
    @Builder.Default
    private LocalDateTime addedDate = LocalDateTime.now();

    @Column(name = "removed_date")
    private LocalDateTime removedDate;

}
