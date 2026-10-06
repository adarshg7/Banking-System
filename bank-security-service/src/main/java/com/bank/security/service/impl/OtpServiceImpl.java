package com.bank.security.service.impl;

import com.bank.common.constants.AppConstants;
import com.bank.common.util.IdGenerator;
import com.bank.security.entity.OtpVerification;
import com.bank.security.repository.OtpVerificationRepository;
import com.bank.security.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OtpServiceImpl implements OtpService {
    private final OtpVerificationRepository otpVerificationRepository;
    public OtpServiceImpl(OtpVerificationRepository otpVerificationRepository){
        this.otpVerificationRepository = otpVerificationRepository;
    }

    @Override
    public String generateOtp(UUID userId, String purpose, String referenceId){

        String otp = IdGenerator.generateOtp(AppConstants.OTP_LENGTH);

        OtpVerification verification = new OtpVerification();
        verification.setUserId(userId);
        verification.setOtpCode(otp);
        verification.setPurpose(purpose);
        verification.setReferenceId(referenceId);
        verification.setExpiryDate(LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRY_MINUTES));

        otpVerificationRepository.save(verification);

        log.info("=== OTP for user {} (purpose: {}): {} (expires in {} min) ===",
                userId, purpose, otp, AppConstants.OTP_EXPIRY_MINUTES);

        return otp;
    }

    @Override
    public boolean verifyOtp(UUID userId, String purpose, String referenceId, String submittedOtp) {
        Optional<OtpVerification> verificationOpt =
                otpVerificationRepository.findTopByUserIdAndPurposeAndReferenceIdAndUsedFalseOrderByExpiryDateDesc(
                        userId, purpose, referenceId);

        if (verificationOpt.isEmpty()) {
            return false;
        }

        OtpVerification verification = verificationOpt.get();

        if (verification.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        verification.setAttempts(verification.getAttempts() + 1);
        if (verification.getAttempts() > AppConstants.MAX_OTP_ATTEMPTS) {
            otpVerificationRepository.save(verification);
            return false;
        }

        boolean matches = verification.getOtpCode().equals(submittedOtp);
        if (matches) {
            verification.setUsed(true);
        }
        otpVerificationRepository.save(verification);

        return matches;
    }


}
