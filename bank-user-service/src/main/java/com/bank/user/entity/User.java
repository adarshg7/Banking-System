package com.bank.user.entity;

import com.bank.common.audit.AuditListener;
import com.bank.user.converter.EncryptedFieldConverter;
import com.bank.user.enums.CustomerType;
import com.bank.user.enums.Role;
import com.bank.user.enums.UserStatus;
import com.bank.user.enums.KycStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users",uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone_number")
})
@EntityListeners({AuditListener.class})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name="middle_name", nullable = false)
    private String middleName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    @Column(name="mother_name", nullable = false)
    private String motherName;

    @Column(name="father_name", nullable = false)
    private String fatherName;

    @Transient  // not a DB column — computed on the fly
    public String getFullName() {
        StringBuilder sb = new StringBuilder(firstName);
        if (middleName != null && !middleName.isBlank()) {
            sb.append(" ").append(middleName);
        }
        sb.append(" ").append(lastName);
        return sb.toString();
    }

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type",nullable = false)
    private CustomerType customerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false)
    @Builder.Default
    private KycStatus kycStatus = KycStatus.PENDING;

    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "pan_number")
    private String panNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "aadhaar_number")
    private String aadhaarNumber;

    @Column(name = "guardian_id")
    private UUID guardianId;

    @Column(name = "company_registration_number")
    private String companyRegistrationNumber;

    @Column(name = "gstin")
    private String gstin;

    @Embedded
    private Address address;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private java.time.LocalDateTime accountLockedUntil;

}
