package com.bank.user.service.impl;

import com.bank.common.util.DateUtils;
import com.bank.user.dto.request.UserRegistrationRequest;
import com.bank.user.dto.response.UserResponse;
import com.bank.user.entity.User;
import com.bank.user.enums.CustomerType;
import com.bank.user.enums.KycStatus;
import com.bank.user.enums.Role;
import com.bank.user.enums.UserStatus;
import com.bank.user.exception.InvalidGuardianException;
import com.bank.user.exception.UserAlreadyExistsException;
import com.bank.user.exception.UserNotFoundException;
import com.bank.user.mapper.UserMapper;
import com.bank.user.repository.UserRepository;
import com.bank.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {

        validateUniqueness(request);
        validateCustomerTypeRules(request);

        User.UserBuilder userBuilder = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .fatherName(request.getFatherName())
                .motherName(request.getMotherName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .customerType(request.getCustomerType())
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .kycStatus(KycStatus.PENDING)
                .panNumber(request.getPanNumber())
                .companyRegistrationNumber(request.getCompanyRegistrationNumber())
                .gstin(request.getGstin());

        if (request.getAddress() != null) {
            userBuilder.address(userMapper.toAddressEntity(request.getAddress()));
        }

        if (request.getCustomerType() == CustomerType.MINOR) {
            User guardian = userRepository.findByEmail(request.getGuardianEmail())
                    .orElseThrow(() -> new InvalidGuardianException(
                            "Guardian with email " + request.getGuardianEmail() + " not found. " +
                                    "Guardian must be a registered INDIVIDUAL customer first."));

            if (guardian.getCustomerType() != CustomerType.INDIVIDUAL) {
                throw new InvalidGuardianException("Guardian must be an INDIVIDUAL customer type");
            }

            userBuilder.guardianId(guardian.getId());
        }

        User savedUser = userRepository.save(userBuilder.build());

        log.info("New user registered: id={}, customerType={}", savedUser.getId(), savedUser.getCustomerType());

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toUserResponse(user);
    }

    private void validateUniqueness(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("A user already exists with email: " + request.getEmail());
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAlreadyExistsException("A user already exists with phone number: " + request.getPhoneNumber());
        }
    }

    /**
     * Business rules that differ per CustomerType — kept in one place so
     * it's obvious what's required for each type as the system grows.
     */
    private void validateCustomerTypeRules(UserRegistrationRequest request) {
        boolean isMinorByAge = DateUtils.isMinor(request.getDateOfBirth());

        switch (request.getCustomerType()) {
            case MINOR -> {
                if (!isMinorByAge) {
                    throw new IllegalArgumentException("Customer type MINOR selected but date of birth indicates an adult");
                }
                if (request.getGuardianEmail() == null || request.getGuardianEmail().isBlank()) {
                    throw new IllegalArgumentException("Guardian email is required for MINOR customer type");
                }
            }
            case INDIVIDUAL, JOINT -> {
                if (isMinorByAge) {
                    throw new IllegalArgumentException("Date of birth indicates a minor — use customer type MINOR instead");
                }
                if (request.getPanNumber() == null || request.getPanNumber().isBlank()) {
                    throw new IllegalArgumentException("PAN number is required for this customer type");
                }
            }
            case BUSINESS, CORPORATE -> {
                if (request.getCompanyRegistrationNumber() == null || request.getCompanyRegistrationNumber().isBlank()) {
                    throw new IllegalArgumentException("Company registration number is required for BUSINESS/CORPORATE accounts");
                }
                if (request.getGstin() == null || request.getGstin().isBlank()) {
                    throw new IllegalArgumentException("GSTIN is required for BUSINESS/CORPORATE accounts");
                }
            }
        }
    }
}