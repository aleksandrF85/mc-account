package com.example.mc_account.web.controllers;


import com.example.mc_account.aop.Loggable;
import com.example.mc_account.dto.*;
import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.dto.response.PagedResponse;
import com.example.mc_account.mapper.AccountMapper;
import com.example.mc_account.model.Account;
import com.example.mc_account.model.StatusCode;
import com.example.mc_account.services.*;
import com.example.mc_account.utils.DtoUtils;
import com.example.mc_account.utils.JwtTokenUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    public final AccountMapper accountMapper;

    public final AccountService accountServiceImpl;

    private final FriendsWebClientService friendsWebClientService;

    private final KafkaProducerService eventProducerService;

    private final AccountEventFactoryService eventFactoryService;

    private final NotificationAsyncService notificationAsyncService;


    @GetMapping("/me")
    @Loggable
    public ResponseEntity<AccountMeDto> getCurrentAccount(@RequestHeader(value = "Authorization") String bearerToken) {

        UUID currentUserId = extractCurrentUserId(bearerToken);
        Account account = accountServiceImpl.findById(currentUserId);

        accountServiceImpl.isOnline(currentUserId, true);

        List<String> friendIds = friendsWebClientService.getFriendsIds(bearerToken);
        if (!friendIds.isEmpty()) {
            notificationAsyncService.notifyFriendBirthdaysAsync(friendIds, account.getId());
        }

        return ResponseEntity.ok(
                accountMapper.accountToMeDto(account));
    }

    @PutMapping("/me")
    @Loggable
    public ResponseEntity<AccountMeDto> updateCurrentAccount(@RequestHeader(value = "Authorization") String bearerToken,
                                                             @RequestBody AccountUpdateDto request) {

        UUID currentUserId = extractCurrentUserId(bearerToken);
        Account updated = accountServiceImpl.update(accountMapper.updateDtoToAccount(request), currentUserId);

        eventProducerService.sendAccountChangesEvent(eventFactoryService.toAccountChangesEvent(request, currentUserId.toString()));

        return ResponseEntity.ok(accountMapper.accountToMeDto(updated));
    }

    @DeleteMapping("/me")
    @Loggable
    public ResponseEntity<Void> markAccountAsDeleted(@RequestHeader(value = "Authorization") String bearerToken) {

        UUID currentUserId = extractCurrentUserId(bearerToken);
        accountServiceImpl.deleteById(currentUserId);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Loggable
    public ResponseEntity<AccountResponseDto> getAccount(@RequestParam String email) {

        return ResponseEntity.ok(
                accountMapper.accountToResponseDto(
                        accountServiceImpl.findByEmail(email)));
    }

    @PostMapping
    @Loggable
    public ResponseEntity<AccountMeDto> createAccount(@RequestBody @Valid AccountMeDto request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                accountMapper.accountToMeDto(
                        accountServiceImpl.create(
                                accountMapper.meDtoToAccount(request))));
    }

    @PostMapping("/lastAction/{id}")
    @Loggable
    public ResponseEntity<Void> lastAction(@PathVariable String id) {

        // Прием UUID от сервиса Dialogs через Webclient о завершении сессии
        // вебсокета у аккаунта: как флаг перехода в статус offline

        accountServiceImpl.isOnline(UUID.fromString(id), false);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Loggable
    public ResponseEntity<AccountDataDto> getAccountById(@PathVariable String id) {

        return ResponseEntity.ok(accountMapper.accountToDataDto(accountServiceImpl.findById(UUID.fromString(id))));
    }

    @DeleteMapping("/{id}")
    @Loggable
    public ResponseEntity<Void> markAccountAsDeletedById(@PathVariable String id) {

        accountServiceImpl.deleteById(UUID.fromString(id));

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @Loggable
    public ResponseEntity<Void> markAccountAsBlockedById(@PathVariable String id) {

        accountServiceImpl.blockById(UUID.fromString(id));

        return ResponseEntity.ok().build();
    }

    @GetMapping("/total")
    @Loggable
    public ResponseEntity<Integer> getTotalAccountsCount() {

        return ResponseEntity.ok(accountServiceImpl.getTotalActiveAccounts());
    }

    @GetMapping("/search")
    @Loggable
    public ResponseEntity<PagedResponse<AccountDataDto>> searchAccounts(
            @RequestHeader(value = "Authorization") String bearerToken,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) List<String> ids,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer ageTo,
            @RequestParam(required = false) Integer ageFrom,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean isDelete,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size) {

        UUID currentUserId = extractCurrentUserId(bearerToken);

        AccountSearchDto request = new AccountSearchDto();
        DtoUtils.setIfNotNull(author, request::setAuthor);
        DtoUtils.setIfNotNull(ids, request::setIds);
        DtoUtils.setIfNotNull(firstName, request::setFirstName);
        DtoUtils.setIfNotNull(lastName, request::setLastName);
        DtoUtils.setIfNotNull(ageTo, request::setAgeTo);
        DtoUtils.setIfNotNull(ageFrom, request::setAgeFrom);
        DtoUtils.setIfNotNull(country, request::setCountry);
        DtoUtils.setIfNotNull(city, request::setCity);
        request.setDeleted(Boolean.TRUE.equals(isDelete));
        request.setCurrentUserId(currentUserId);

        Pageable pageable = PageRequest.of(page, size);

        Page<Account> filtered = accountServiceImpl.search(request, pageable);
        List<AccountDataDto> content = filtered.map(accountMapper::accountToDataDto).getContent();

        PagedResponse<AccountDataDto> response = new PagedResponse<>();
        response.setContent(content);
        response.setTotalPages(filtered.getTotalPages());
        response.setTotalElements(filtered.getTotalElements());
        response.setPageNumber(filtered.getNumber());
        response.setPageSize(filtered.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/statusCode")
    @Loggable
    public ResponseEntity<PagedResponse<AccountDataDto>> searchByStatusCode(
            @RequestHeader(value = "Authorization") String bearerToken,
            @RequestParam(required = false) String statusCode,
            @RequestParam(required = false) String firstName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        if (statusCode == null || statusCode.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Pageable pageable = PageRequest.of(page, size);
        StatusCode sc = StatusCode.valueOf(statusCode);

        List<String> friendIds = friendsWebClientService.getIdsByStatusCode(bearerToken, statusCode);

        AccountSearchDto request = new AccountSearchDto();
        DtoUtils.setIfNotNull(firstName, request::setFirstName);
        request.setIds(friendIds);
        request.setDeleted(false);

        Page<Account> filtered = accountServiceImpl.search(request, pageable);
        List<AccountDataDto> content = filtered
                .map(account -> {
                    AccountDataDto dto = accountMapper.accountToDataDto(account);
                    dto.setStatusCode(sc);
                    return dto;
                }).getContent();

        PagedResponse<AccountDataDto> response = new PagedResponse<>();
        response.setContent(content);
        response.setTotalPages(filtered.getTotalPages());
        response.setTotalElements(filtered.getTotalElements());
        response.setPageNumber(filtered.getNumber());
        response.setPageSize(filtered.getSize());

        return ResponseEntity.ok(response);    }

    private UUID extractCurrentUserId(String bearerToken) {
        String email = JwtTokenUtils.parseJwtToken(bearerToken).get("sub").toString();
        return accountServiceImpl.findByEmail(email).getId();
    }
}
