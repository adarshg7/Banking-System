package com.bank.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateRequest {
    @NotNull
    private String role; // MAKER, CHECKER, AUTHORIZER, VIEWER, ADMIN, CUSTOMER
}