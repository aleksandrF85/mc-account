package com.example.mc_account.services;

import com.example.mc_account.dto.filter.AccountByFilterDto;
import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.dto.filter.PageFilter;
import com.example.mc_account.model.Account;

import java.util.List;

public interface AccountService {


    List<Account> filterBy(AccountByFilterDto filter);

    List<Account> search(AccountSearchDto filter, PageFilter pageFilter);

    List<Account> findByIds(List<Long> ids, PageFilter pageFilter);

    List<Account> findAll();

    List<Account> findAll(PageFilter pageFilter);

    Account findById(Long id);

    Account create(Account account);

    Account update(Account account);

    void deleteById(Long id);
}
