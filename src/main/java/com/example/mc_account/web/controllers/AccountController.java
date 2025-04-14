package com.example.mc_account.web.controllers;


import com.example.mc_account.dto.AccountDataDto;
import com.example.mc_account.dto.AccountMeDto;
import com.example.mc_account.dto.AccountResponseDto;
import com.example.mc_account.dto.AccountUpdateDto;
import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.dto.filter.PageFilter;
import com.example.mc_account.mapper.AccountMapper;
import com.example.mc_account.model.Account;
import com.example.mc_account.model.StatusCode;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.utils.JwtTokenUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    public final AccountMapper accountMapper;

    public final AccountService accountServiceImpl;

    @GetMapping("/me")
    public ResponseEntity<AccountMeDto> getCurrentAccount(@RequestHeader(value = "Authorization") String bearerToken) {

        Map<String, Object> claims = JwtTokenUtils.parseJwtToken(bearerToken);
        String email = claims.get("sub").toString();

        if (accountServiceImpl.existsByEmail(email)){
            Account account = accountServiceImpl.findByEmail(email);

            accountServiceImpl.isOnline(account.getId(), true);
            //TODO Уточнить когда помечать аккаунт online (при входе?)

            return ResponseEntity.ok(
                    accountMapper.accountToMeDto(account));
        }

        //TODO Создавать account из kafka event
        AccountMeDto accountMeDto = new AccountMeDto();
        accountMeDto.setEmail(email);
        accountMeDto.setFirstName(claims.get("firstName").toString());
        accountMeDto.setLastName(claims.get("lastName").toString());
        accountMeDto.setId(claims.get("userId").toString());
        accountMeDto.setRegDate(LocalDateTime.now());
        accountMeDto.setDeleted(false);
        accountMeDto.setBlocked(false);
        accountMeDto.setOnline(true);

        return ResponseEntity.ok(
                accountMapper.accountToMeDto(
                        accountServiceImpl.create(
                                accountMapper.meDtoToAccount(accountMeDto))));
    }

    @PutMapping("/me")
    public ResponseEntity<AccountMeDto> updateCurrentAccount(@RequestHeader(value = "Authorization") String bearerToken,
                                                             @RequestBody AccountUpdateDto request) {

        //TODO Уточнить способ приема токена и его расшифровки
        String email = JwtTokenUtils.parseJwtToken(bearerToken).get("sub").toString();
        UUID id = accountServiceImpl.findByEmail(email).getId();

        return ResponseEntity.ok(
                accountMapper.accountToMeDto(
                        accountServiceImpl.update(accountMapper.updateDtoToAccount(request), id)));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> markAccountAsDeleted(@RequestHeader(value = "Authorization") String bearerToken) {

        //TODO Уточнить способ приема токена и его расшифровки
        String email = JwtTokenUtils.parseJwtToken(bearerToken).get("sub").toString();
        UUID id = accountServiceImpl.findByEmail(email).getId();

        accountServiceImpl.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<AccountResponseDto> getAccount(@RequestParam String email) {

        return ResponseEntity.ok(
                accountMapper.accountToResponseDto(
                        accountServiceImpl.findByEmail(email)));
    }

    @PostMapping
    public ResponseEntity<AccountMeDto> createAccount(@RequestBody @Valid AccountMeDto request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                accountMapper.accountToMeDto(
                        accountServiceImpl.create(
                                accountMapper.meDtoToAccount(request))));
    }

    @PostMapping("/lastAction/{id}")
    public ResponseEntity<Void> lastAction(@PathVariable String id) {

        //TODO Прием UUID от сервиса Dialogs через Webclient
        // о завершении сессии вебсокета у аккаунта: как
        // флаг перехода в статус offline

        accountServiceImpl.isOnline(UUID.fromString(id), false);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDataDto> getAccountById(@PathVariable String id) {

        return ResponseEntity.ok(accountMapper.accountToDataDto(accountServiceImpl.findById(UUID.fromString(id))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> markAccountAsDeletedById(@PathVariable String id) {

        accountServiceImpl.deleteById(UUID.fromString(id));

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> markAccountAsBlockedById(@PathVariable String id) {

        accountServiceImpl.blockById(UUID.fromString(id));

        return ResponseEntity.ok().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalAccountsCount() {

        return ResponseEntity.ok(accountServiceImpl.findAll().size());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccountDataDto>> searchAccounts(@RequestBody AccountSearchDto request,
                                                               @Valid PageFilter pageFilter) {

        //TODO Уточнить дополнительные параметры поиска и ответ (должен быть Page?)


        return ResponseEntity.ok(
                accountServiceImpl.search(request, pageFilter).stream()
                        .map(accountMapper::accountToDataDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/search/statusCode")
    public ResponseEntity<List<AccountDataDto>> searchByStatusCode(@RequestParam StatusCode statusCode,
                                                                   @Valid PageFilter pageFilter) {

        //TODO Уточнить дополнительные параметры поиска и ответ (должен быть Page?)

        AccountSearchDto request = new AccountSearchDto();
        request.setStatusCode(statusCode);

        return ResponseEntity.ok(
                accountServiceImpl.search(request, pageFilter).stream()
                        .map(accountMapper::accountToDataDto)
                        .collect(Collectors.toList()));
    }

}
