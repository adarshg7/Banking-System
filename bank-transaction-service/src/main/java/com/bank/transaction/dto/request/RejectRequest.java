package com.bank.transaction.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectRequest {
    private String reason;
}