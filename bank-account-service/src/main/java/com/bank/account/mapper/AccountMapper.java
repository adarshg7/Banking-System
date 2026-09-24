package com.bank.account.mapper;

import com.bank.account.client.UserLookupClient;
import com.bank.account.dto.response.AccountHolderResponse;
import com.bank.account.dto.response.AccountResponse;
import com.bank.account.entity.Account;
import com.bank.account.entity.AccountHolder;
import com.bank.user.dto.response.UserResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountMapper {

    private final UserLookupClient userLookupClient;

    public AccountMapper(UserLookupClient userLookupClient) {
        this.userLookupClient = userLookupClient;
    }

    public AccountResponse toAccountResponse(Account account, List<AccountHolder> holders) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setStatus(account.getStatus());
        response.setOperationMode(account.getOperationMode());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setIfscCode(account.getIfscCode());
        response.setHolders(toAccountHolderResponseList(holders));
        return response;
    }

    private List<AccountHolderResponse> toAccountHolderResponseList(List<AccountHolder> holders) {
        List<AccountHolderResponse> result = new ArrayList<>();
        for (AccountHolder holder : holders) {
            result.add(toAccountHolderResponse(holder));
        }
        return result;
    }

    private AccountHolderResponse toAccountHolderResponse(AccountHolder holder) {
        UserResponse user = userLookupClient.getUser(holder.getUserId());

        AccountHolderResponse response = new AccountHolderResponse();
        response.setUserId(holder.getUserId());
        response.setFullName(user.getFullName());
        response.setHolderRelation(holder.getHolderRelation());
        response.setCanOperate(holder.isCanOperate());
        response.setPrimaryHolder(holder.isPrimaryHolder());
        return response;
    }
}