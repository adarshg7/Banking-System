package com.bank.account.dto.response;

import com.bank.account.enums.HolderRelation;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AccountHolderResponse {

    private UUID userId;
    private String fullName;
    private HolderRelation holderRelation;
    private boolean canOperate;
    private boolean primaryHolder;
}