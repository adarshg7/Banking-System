package com.bank.account.config;

import com.bank.account.entity.Branch;
import com.bank.account.repository.BranchRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final BranchRepository branchRepository;

    public DataSeeder(BranchRepository branchRepository){
        this.branchRepository = branchRepository;
    }

    @Override
    public void run(String... args){
        if(branchRepository.count() == 0){
            Branch mainBranch = new Branch();

            mainBranch.setBranchCode("0001");
            mainBranch.setBranchName("Main Branch");
            mainBranch.setIfscCode("BANK0000001");
            mainBranch.setCity("Mumbai");
            mainBranch.setState("Maharashtra");
            mainBranch.setAddress("123 Banking Street, Mumbai");
            branchRepository.save(mainBranch);

        }
    }
}
