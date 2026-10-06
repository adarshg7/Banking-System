package com.bank.account.entity;


import com.bank.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
public class Branch extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "branch_code", nullable = false, unique = true, length = 4)
    private String branchCode; // e.g. "0001" — used inside account number

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "ifsc_code", nullable = false, unique = true, length = 11)
    private String ifscCode; // e.g. "BANK0000001"

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "address")
    private String address;
}