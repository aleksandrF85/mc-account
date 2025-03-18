package com.example.mc_account.web.controllers;


import com.example.mc_account.dto.*;
import com.example.mc_account.dto.filter.AccountByFilterDto;
import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.dto.filter.PageFilter;
import com.example.mc_account.mapper.AccountMapper;
import com.example.mc_account.model.Account;
import com.example.mc_account.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    public final AccountMapper accountMapper;

    public final AccountService accountServiceImpl;

    @PutMapping("/recovery")
    public ResponseEntity<String> recoveryUserAccount(@RequestBody AccountRecoveryRq request){

        //TODO написать метод для восстановления аккаунта

        return ResponseEntity.ok("string");
    }

    @GetMapping("/me")
    public ResponseEntity<AccountDto> getUserAccount(){

        Long id = 1L; //TODO получить id из AuthenticationPrincipal

        return ResponseEntity.ok(accountMapper.accountToDto(accountServiceImpl.findById(id)));
    }

    @PutMapping("/me")
    public ResponseEntity<AccountDto> updateUserAccount(@RequestBody AccountDto request){


        Long id = 1L; //TODO получить id из AuthenticationPrincipal

        return ResponseEntity.ok(accountMapper.accountToDto(accountServiceImpl.update(accountMapper.dtoToAccount(id, request))));
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUserAccount(){

        Long id = 1L; //TODO получить id из AuthenticationPrincipal
        accountServiceImpl.deleteById(id);

        return ResponseEntity.ok("Account with id " + id + " deleted.");
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<String> blockAccountById(@PathVariable Long id) {

        //TODO написать метод блокировки аккаунта

        return ResponseEntity.ok("string");
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<String> unblockAccountById(@PathVariable Long id) {

        //TODO написать метод разблокировки аккаунта

        return ResponseEntity.ok("string");
    }

    @GetMapping
    public ResponseEntity<String> getAllAccounts(@Valid PageFilter pageFilter) {

        accountServiceImpl.findAll(pageFilter).stream()
                .map(accountMapper::accountToDto)
                .collect(Collectors.toList());

        //TODO уточнить тело ответа

        return ResponseEntity.ok("Success!");
    }

    @PostMapping
    public ResponseEntity<Void> createAccount(@RequestBody AccountDto request) {

        accountServiceImpl.create(accountMapper.dtoToAccount(request));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/searchByFilter")
    public ResponseEntity<List<AccountDto>> searchAccountByFilter(@RequestBody @Valid AccountByFilterDto request) {

        //TODO уточнить какой должен быть запрос GET???

        return ResponseEntity.ok(
                accountServiceImpl.filterBy(request).stream()
                        .map(accountMapper::accountToDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {

        return ResponseEntity.ok(accountMapper.accountToDto(accountServiceImpl.findById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccountDto>> searchAccount(@RequestBody AccountSearchDto request, @Valid PageFilter pageFilter) {

        return ResponseEntity.ok(
                accountServiceImpl.search(request, pageFilter).stream()
                        .map(accountMapper::accountToDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/ids")
    public ResponseEntity<String> getAllIds() {

        var ids = accountServiceImpl.findAll().stream().map(Account::getId).toList();

        return ResponseEntity.ok("All account ID: " + ids);
    }

    @GetMapping("/accountIds")
    public ResponseEntity<List<AccountDto>> getAccountIds(@RequestBody List<Long> ids, @Valid PageFilter pageFilter) {

        return ResponseEntity.ok(
                accountServiceImpl.findByIds(ids, pageFilter).stream()
                        .map(accountMapper::accountToDto)
                        .collect(Collectors.toList()));
    }
}
