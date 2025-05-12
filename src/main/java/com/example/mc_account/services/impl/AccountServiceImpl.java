package com.example.mc_account.services.impl;


import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.exception.AlreadyExistException;
import com.example.mc_account.model.Account;
import com.example.mc_account.reposirory.AccountRepository;
import com.example.mc_account.reposirory.AccountSpecification;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.utils.BeanUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    public List<Account> search(AccountSearchDto searchFilter, Integer page, Integer size) {
        return repository.findAll(
                AccountSpecification.withFilter(searchFilter),
                PageRequest.of(page, size)).getContent();
    }

    @Override
    public List<Account> findAll() {
        return repository.findAll();
    }

    @Override
    public Account findByEmail(String email) {
         Account account = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(
                        "Пользователь с таким email {0} не найден!", email
                )));

        if (account.isDeleted()) {
            throw new EntityNotFoundException(MessageFormat.format(
                    "Пользователь с таким email {0} удален!", email));
        }

        return account;
    }

    @Override
    public boolean existsByEmail(String email) {

        return repository.existsByEmail(email);
    }

    @Override
    public Account findById(UUID id) {

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(
                        "Пользователь с таким ID {0} не найден!", id
                )));
    }

    @Override
    public Account create(Account account) {

        if (repository.existsByEmail(account.getEmail())) {
            throw new AlreadyExistException(MessageFormat.format(
                    "Пользователь с таким email {0} уже существует!", account.getEmail()
            ));
        }

        return repository.save(account);
    }

    @Override
    public Account update(Account account, UUID id) {

        Account accountForUpdate = findById(id);

        BeanUtils.copyNonNullProperties(account, accountForUpdate);

        return repository.save(accountForUpdate);
    }

    @Override
    public void deleteById(UUID id) {

        Account account = findById(id);
        account.setDeleted(true);
        account.setDeletionTimestamp(LocalDateTime.now());
        repository.save(account);
    }

    @Override
    public void blockById(UUID id) {

        Account account = findById(id);
        account.setBlocked(true);
        repository.save(account);
    }

    @Override
    public void isOnline(UUID id, boolean isOnline) {

        Account account = findById(id);

        if (isOnline) {
            account.setOnline(true);
            account.setLastOnlineTime(null);
        } else {
            account.setLastOnlineTime(LocalDateTime.now());
            account.setOnline(false);
        }

        repository.save(account);
    }

}
