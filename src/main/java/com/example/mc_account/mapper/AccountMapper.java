package com.example.mc_account.mapper;

import com.example.mc_account.dto.AccountDto;
import com.example.mc_account.model.Account;
import org.mapstruct.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    Account dtoToAccount(AccountDto request);

    Account dtoToAccount(Long id, AccountDto request);

    AccountDto accountToDto(Account account);

}
