package com.example.mc_account.services;

import com.example.mc_account.events.*;
import com.example.mc_account.mapper.AccountMapper;
import com.example.mc_account.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountEventHandlerService {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public void handleUserRegistrationEvent(UserRegistrationEvent event) {
        Account account = accountMapper.userRegistrationToAccount(event.getUserRegistration());
        accountService.create(account);
        log.info("Account created: " + account);
    }

    public void handleResetPasswordEvent(ResetPasswordEvent event) {
        ResetPassword resetPassword = event.getResetPassword();
        Account account = accountService.findByEmail(resetPassword.getEmail());
        account.setPassword(resetPassword.getToken()); // уточнить источник пароля
        accountService.update(account, account.getId());
        log.info("Account updated: " + account);
    }

    public void handleChangeEmailEvent(ChangeEmailEvent event) {
        ChangeEmail changeEmail = event.getChangeEmail();
        Account account = accountService.findById(UUID.fromString(changeEmail.getUserId()));
        account.setEmail(changeEmail.getEmail());
        accountService.update(account, account.getId());
        log.info("Account updated: " + account);
    }
}
