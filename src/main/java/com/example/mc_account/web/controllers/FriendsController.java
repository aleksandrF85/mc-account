package com.example.mc_account.web.controllers;

import com.example.mc_account.aop.Loggable;
import com.example.mc_account.dto.AccountDataDto;
import com.example.mc_account.dto.FriendDto;
import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.dto.response.PagedResponse;
import com.example.mc_account.mapper.AccountMapper;
import com.example.mc_account.model.Account;
import com.example.mc_account.model.StatusCode;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.services.FriendsWebClientService;
import com.example.mc_account.utils.DtoUtils;
import com.example.mc_account.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendsController {

    public final AccountMapper accountMapper;

    public final AccountService accountServiceImpl;

    private final FriendsWebClientService friendsWebClientService;


    @GetMapping
    @Loggable
    public ResponseEntity<PagedResponse<FriendDto>> getFriends(
            @RequestHeader(value = "Authorization") String bearerToken,
            @RequestParam(required = false) String statusCode,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) Integer ageTo,
            @RequestParam(required = false) Integer ageFrom,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size) {

        if (statusCode == null || statusCode.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        AccountSearchDto request = new AccountSearchDto();
        DtoUtils.setIfNotNull(firstName, request::setFirstName);
        DtoUtils.setIfNotNull(ageTo, request::setAgeTo);
        DtoUtils.setIfNotNull(ageFrom, request::setAgeFrom);
        DtoUtils.setIfNotNull(country, request::setCountry);
        DtoUtils.setIfNotNull(city, request::setCity);
        request.setDeleted(false);

        Pageable pageable = PageRequest.of(page, size);
        StatusCode sc = StatusCode.valueOf(statusCode);

        List<String> friendIds = friendsWebClientService.getIdsByStatusCode(bearerToken, statusCode);
        request.setIds(friendIds);

        Page<Account> filtered = accountServiceImpl.search(request, pageable);

        Page<FriendDto> content = filtered.map(account -> {
            FriendDto dto = accountMapper.accountToFriendDto(account);
            dto.setStatusCode(sc);
            return dto;
        });

        return ResponseEntity.ok(PaginationUtils.toPagedResponse(content));
    }

}
