package com.example.mc_account.services;

import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.model.Account;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AccountService {


    List<Account> search(AccountSearchDto filter, Pageable pageable);

    List<Account> findAll();

    Account findById(UUID id);

    Account create(Account account);

    Account update(Account account, UUID id);

    void deleteById(UUID id);

    Account findByEmail(String email);
}
