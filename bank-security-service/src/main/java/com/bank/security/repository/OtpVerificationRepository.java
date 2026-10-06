package com.bank.security.repository;

import com.bank.security.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {
    Optional<OtpVerification> findTopByUserIdAndPurposeAndReferenceIdAndUsedFalseOrderByExpiryDateDesc(UUID userId, String purpose, String referenceId);
}
