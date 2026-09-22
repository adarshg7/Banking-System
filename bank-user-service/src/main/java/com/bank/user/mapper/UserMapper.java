package com.bank.user.mapper;

import com.bank.user.dto.request.AddressRequest;
import com.bank.user.dto.response.UserResponse;
import com.bank.user.entity.Address;
import com.bank.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    Address toAddressEntity(AddressRequest addressRequest);

    @Mapping(
            target = "maskedPan",
            expression = "java(com.bank.common.util.MaskingUtils.maskPan(user.getPanNumber()))"
    )
    @Mapping(
            target = "fullName",
            expression = "java(user.getFullName())"
    )
    UserResponse toUserResponse(User user);
}