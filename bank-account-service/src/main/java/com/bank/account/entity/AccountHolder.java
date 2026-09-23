package com.bank.account.entity;

import com.bank.account.enums.HolderRelation;
import com.bank.common.audit.Auditable;
import com.bank.common.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_holders")
@Getter
@Setter
@NoArgsConstructor
public class AccountHolder extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "holder_relation", nullable = false)
    private HolderRelation holderRelation;

    @Column(name = "is_primary_holder", nullable = false)
    private boolean primaryHolder = false;

    @Column(name = "can_operate", nullable = false)
    private boolean canOperate = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "added_date", nullable = false)
    private LocalDateTime addedDate = LocalDateTime.now();

    @Column(name = "removed_date")
    private LocalDateTime removedDate;
}