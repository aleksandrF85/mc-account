package com.example.mc_account.web.controllers;


import com.example.mc_account.aop.Loggable;
import com.example.mc_account.dto.AccountDataDto;
import com.example.mc_account.dto.AccountMeDto;
import com.example.mc_account.dto.AccountResponseDto;
import com.example.mc_account.dto.AccountUpdateDto;
import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.mapper.AccountMapper;
import com.example.mc_account.model.Account;
import com.example.mc_account.model.StatusCode;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.services.KafkaService;
import com.example.mc_account.utils.DtoUtils;
import com.example.mc_account.utils.JwtTokenUtils;
import com.example.mc_account.events.AccountChanges;
import com.example.mc_account.events.AccountChangesEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    public final AccountMapper accountMapper;

    public final AccountService accountServiceImpl;

    public final KafkaService kafkaServiceImpl;

    @GetMapping("/me")
    @Loggable
    public ResponseEntity<AccountMeDto> getCurrentAccount(@RequestHeader(value = "Authorization") String bearerToken) {

        Map<String, Object> claims = JwtTokenUtils.parseJwtToken(bearerToken);
        String email = claims.get("sub").toString();

        Account account = accountServiceImpl.findByEmail(email);

//        accountServiceImpl.isOnline(account.getId(), true);
        //TODO Уточнить когда помечать аккаунт online (при входе?)

        return ResponseEntity.ok(
                accountMapper.accountToMeDto(account));
    }

    @PutMapping("/me")
    @Loggable
    public ResponseEntity<AccountMeDto> updateCurrentAccount(@RequestHeader(value = "Authorization") String bearerToken,
                                                             @RequestBody AccountUpdateDto request) {

        String email = JwtTokenUtils.parseJwtToken(bearerToken).get("sub").toString();
        UUID id = accountServiceImpl.findByEmail(email).getId();

        sendAccountChangesEvent(request, id.toString());

        return ResponseEntity.ok(
                accountMapper.accountToMeDto(
                        accountServiceImpl.update(accountMapper.updateDtoToAccount(request), id)));
    }

    @DeleteMapping("/me")
    @Loggable
    public ResponseEntity<Void> markAccountAsDeleted(@RequestHeader(value = "Authorization") String bearerToken) {

        String email = JwtTokenUtils.parseJwtToken(bearerToken).get("sub").toString();
        UUID id = accountServiceImpl.findByEmail(email).getId();

        accountServiceImpl.deleteById(id);

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

        //TODO Прием UUID от сервиса Dialogs через Webclient
        // о завершении сессии вебсокета у аккаунта: как
        // флаг перехода в статус offline

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

        return ResponseEntity.ok(accountServiceImpl.findAll().size());
    }

    @GetMapping("/search")
    @Loggable
    public ResponseEntity<PageImpl<AccountDataDto>> searchAccounts(@RequestParam(required = false)  String author,
                                                                   @RequestParam(required = false)  List<String> ids,
                                                                   @RequestParam(required = false)  String firstName,
                                                                   @RequestParam(required = false)  String lastName,
                                                                   @RequestParam(required = false)  Integer ageTo,
                                                                   @RequestParam(required = false)  Integer ageFrom,
                                                                   @RequestParam(required = false)  String country,
                                                                   @RequestParam(required = false)  String city,
                                                                   @RequestParam(required = false)  String statusCode,
                                                                   @RequestParam(required = false)  boolean isDelete,
                                                                   @RequestParam(required = false, defaultValue = "0") String page,
                                                                   @RequestParam(required = false, defaultValue = "5") String size) {

        AccountSearchDto request = new AccountSearchDto();

        DtoUtils.setIfNotNull(author, request::setAuthor);
        DtoUtils.setIfNotNull(ids, request::setIds);
        DtoUtils.setIfNotNull(firstName, request::setFirstName);
        DtoUtils.setIfNotNull(lastName, request::setLastName);
        DtoUtils.setIfNotNull(ageTo, request::setAgeTo);
        DtoUtils.setIfNotNull(ageFrom, request::setAgeFrom);
        DtoUtils.setIfNotNull(country, request::setCountry);
        DtoUtils.setIfNotNull(city, request::setCity);
        DtoUtils.setIfNotNull(statusCode, code -> Enum.valueOf(StatusCode.class, code), request::setStatusCode);
        request.setDeleted(isDelete);

        //TODO Уточнить дополнительные параметры поиска и ответ (должен быть Page?)

        List<AccountDataDto> accountDataDtoList = accountServiceImpl.search(request).stream()
                .map(accountMapper::accountToDataDto)
                .collect(Collectors.toList());
        Pageable pageRequest = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), accountDataDtoList.size());

        List<AccountDataDto> pageContent = accountDataDtoList.subList(start, end);
        return ResponseEntity.ok(new PageImpl<>(pageContent, pageRequest, accountDataDtoList.size()));
    }

    @GetMapping("/search/statusCode")
    @Loggable
    public ResponseEntity<PageImpl<AccountDataDto>> searchByStatusCode(@RequestParam(required = false)  String statusCode,
                                                                   @RequestParam(required = false, defaultValue = "0") String page,
                                                                   @RequestParam(required = false, defaultValue = "5")  String size) {

        //TODO Уточнить дополнительные параметры поиска и ответ (должен быть Page?)

        AccountSearchDto request = new AccountSearchDto();
        request.setStatusCode(Enum.valueOf(StatusCode.class, statusCode));

        List<AccountDataDto> accountDataDtoList = accountServiceImpl.search(request).stream()
                .map(accountMapper::accountToDataDto)
                .collect(Collectors.toList());
        Pageable pageRequest = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), accountDataDtoList.size());

        List<AccountDataDto> pageContent = accountDataDtoList.subList(start, end);
        return ResponseEntity.ok(new PageImpl<>(pageContent, pageRequest, accountDataDtoList.size()));
    }

    private void sendAccountChangesEvent(AccountUpdateDto request, String id) {

        AccountChangesEvent event = new AccountChangesEvent();

        event.setAccountChanges(new AccountChanges(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getPhoto(),
                request.getAbout(),
                request.getCity(),
                request.getCountry(),
                request.getBirthDate(),
                request.getEmojiStatus()
        ));

        kafkaServiceImpl.sendUserRegistrationEvent(event);

    }
}
