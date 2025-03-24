package com.example.mc_account.services.impl;


import com.example.mc_account.dto.filter.AccountByFilterDto;
import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.reposirory.AccountRepository;
import com.example.mc_account.reposirory.AccountSpecification;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.dto.filter.PageFilter;
import com.example.mc_account.exception.AlreadyExistException;
import com.example.mc_account.model.Account;
import com.example.mc_account.utils.BeanUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    public List<Account> filterBy(AccountByFilterDto filter) {

        return repository.findAll(
                AccountSpecification.withFilter(filter.getAccountSearchDto()),
                        PageRequest.of(filter.getSize(), filter.getPage())).getContent();
    }
    @Override
    public List<Account> search(AccountSearchDto searchFilter, PageFilter pageFilter) {
        return repository.findAll(
                        AccountSpecification.withFilter(searchFilter),
                        PageRequest.of(
                                pageFilter.getSize(),
                                pageFilter.getPage(),
                                Sort.by(pageFilter.getSort()))).getContent();
    }

    @Override
    public List<Account> findByIds(List<Long> ids, PageFilter pageFilter) {

        AccountSearchDto searchFilter = new AccountSearchDto();
        searchFilter.setIds(ids);

        return repository.findAll(
                AccountSpecification.withFilter(searchFilter),
                PageRequest.of(
                        pageFilter.getSize(),
                        pageFilter.getPage(),
                        Sort.by(pageFilter.getSort()))).getContent();
    }
    @Override
    public List<Account> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Account> findAll(PageFilter pageFilter){

        return repository.findAll(PageRequest.of(
                        pageFilter.getSize(),
                        pageFilter.getPage(),
                        Sort.by(pageFilter.getSort()))).toList();
    }

    @Override
    public Account findById(Long id) {

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
    public Account update(Account account) {
        Account accountForUpdate = findById(account.getId());

        if (account.getEmail() != null && !account.getEmail().equals(accountForUpdate.getEmail())){
            if (repository.existsByEmail(account.getEmail())) {
                throw new AlreadyExistException(MessageFormat.format(
                        "Пользователь с таким email {0} уже существует!", account.getEmail()
                ));
            }
        }

        BeanUtils.copyNonNullProperties(account, accountForUpdate);

        return repository.save(accountForUpdate);
    }

    @Override
    public void deleteById(Long id) {

        repository.deleteById(id);
    }
}
