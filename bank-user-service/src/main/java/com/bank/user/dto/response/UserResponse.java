package com.bank.user.dto.response;

import com.bank.user.enums.CustomerType;
import com.bank.user.enums.KycStatus;
import com.bank.user.enums.Role;
import com.bank.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Role role;
    private CustomerType customerType;
    private UserStatus status;
    private KycStatus kycStatus;
    private String maskedPan;
    private UUID guardianId;
}