package com.bank.account.repository;

import com.bank.account.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    Optional<Branch> findByBranchCode(String branchCode);
}