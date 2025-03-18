package com.example.mc_account.web.controllers;

import com.example.mc_account.dto.AccountDto;
import com.example.mc_account.dto.StorageBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    @PostMapping
    public ResponseEntity<AccountDto> uploadFile(@RequestBody StorageBody body) {

        AccountDto response = new AccountDto();

        return ResponseEntity.ok(response);
    }
}
