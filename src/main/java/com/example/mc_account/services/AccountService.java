package com.example.mc_account.services;

import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AccountService {


    Page<Account> search(AccountSearchDto filter, Pageable pageable);

    List<Account> findAll();

    Account findById(UUID id);

    Account create(Account account);

    Account update(Account account, UUID id);

    void deleteById(UUID id);

    void blockById(UUID id);

    void isOnline(UUID id, boolean isOnline);

    Account findByEmail(String email);

    boolean existsByEmail(String email);

    List<Account> findAllByIds(List<String> ids);

    Page<Account> searchFilteredAccounts(
            AccountSearchDto dto,
            UUID currentUserId,
            String statusCode,
            List<String> friendIds,
            Pageable pageable
    );

    Page<Account> searchFriendsByStatusCode(
            List<String> friendIds,
            String statusCode,
            Pageable pageable
    );

    int getTotalActiveAccounts();

}
