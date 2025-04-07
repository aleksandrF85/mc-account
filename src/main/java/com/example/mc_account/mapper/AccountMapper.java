package com.example.mc_account.mapper;

import com.example.mc_account.dto.AccountDataDto;
import com.example.mc_account.dto.AccountMeDto;
import com.example.mc_account.dto.AccountResponseDto;
import com.example.mc_account.dto.AccountUpdateDto;
import com.example.mc_account.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    Account meDtoToAccount(AccountMeDto request);

    Account updateDtoToAccount(AccountUpdateDto request);

    AccountResponseDto accountToResponseDto(Account response);

    AccountMeDto accountToMeDto(Account response);

    AccountDataDto accountToDataDto(Account response);

}
