package com.example.mc_account.services.impl;


import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.exception.AlreadyExistException;
import com.example.mc_account.model.Account;
import com.example.mc_account.reposirory.AccountRepository;
import com.example.mc_account.reposirory.AccountSpecification;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.services.OnlineStatusScheduler;
import com.example.mc_account.utils.BeanUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository repository;
    @Autowired
    private OnlineStatusScheduler onlineStatusScheduler;

    @Override
    public Page<Account> search(AccountSearchDto searchFilter, Pageable pageable) {
        return repository.findAll(AccountSpecification.withFilter(searchFilter), pageable);
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
        account.setDeletionTimestamp(OffsetDateTime.now());
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
        OffsetDateTime now = OffsetDateTime.now();

        account.setLastOnlineTime(now);
        account.setOnline(isOnline);
        repository.save(account);

        if (isOnline) {
            // Асинхронное отключение пользователя через 3 минуты
            onlineStatusScheduler.scheduleOffline(id, Duration.ofMinutes(3));
        }
    }

    @Override
    public List<Account> findAllByIds(List<String> ids) {

        List<UUID> uuidList = ids.stream().map(UUID::fromString).toList();

        return repository.findAllById(uuidList);
    }

    @Override
    public int getTotalActiveAccounts() {
        long count = repository.countActiveAccounts();
        if (count > Integer.MAX_VALUE) {
            throw new IllegalStateException("Слишком много аккаунтов для int: " + count);
        }
        return (int) count;
    }
}
