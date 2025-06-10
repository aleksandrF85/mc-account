package com.example.mc_account.mapper;

import com.example.mc_account.dto.AccountDataDto;
import com.example.mc_account.dto.AccountMeDto;
import com.example.mc_account.dto.AccountResponseDto;
import com.example.mc_account.dto.AccountUpdateDto;
import com.example.mc_account.events.UserRegistration;
import com.example.mc_account.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    Account meDtoToAccount(AccountMeDto request);

    Account updateDtoToAccount(AccountUpdateDto request);

    AccountResponseDto accountToResponseDto(Account response);

    AccountMeDto accountToMeDto(Account response);

    AccountDataDto accountToDataDto(Account response);

    @Mapping(target = "id", expression = "java(UUID.fromString(registration.getUserId()))")
    @Mapping(target = "role", expression = "java(Set.of(RoleType.valueOf(registration.getRole())))")
    @Mapping(target = "regDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "statusCode", constant = "NONE")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "blocked", constant = "false")
    @Mapping(target = "online", constant = "false")
    Account userRegistrationToAccount(UserRegistration registration);

}
