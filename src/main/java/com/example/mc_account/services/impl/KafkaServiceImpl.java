package com.example.mc_account.services.impl;

import com.skillbox.auth.dto.events.ResetPassword;
import com.skillbox.auth.dto.events.ResetPasswordEvent;
import com.skillbox.auth.dto.events.UserRegistration;
import com.skillbox.auth.dto.events.UserRegistrationEvent;
import com.example.mc_account.model.Account;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.services.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {


    private final AccountService accountServiceImpl;

    @SneakyThrows
    @KafkaListener(topics = "${app.kafka.userRegistration}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "userRegistrationEventKafkaListenerContainerFactory")
    @Override
    public void createAccountByUserRegistrationEvent(UserRegistrationEvent event) {


        log.info("Received event: {}", event.getUserRegistration().toString());

        Account account = new Account();
        UserRegistration userRegistration = event.getUserRegistration();

        account.setEmail(userRegistration.getEmail());
        account.setFirstName(userRegistration.getFirstName());
        account.setLastName(userRegistration.getLastName());
        account.setId(UUID.fromString(userRegistration.getUserId()));
        account.setRegDate(LocalDateTime.now());
        account.setDeleted(false);
        account.setBlocked(false);
        account.setOnline(false);
        account.setPassword(userRegistration.getUserId()); //TODO Уточнить откуда получать пароль
        log.info(account.getPassword());

        accountServiceImpl.create(account);

    }

    @KafkaListener(topics = "${app.kafka.resetPassword}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "resetPasswordEventKafkaListenerContainerFactory")
    @Override
    public void resetPassword(ResetPasswordEvent event) {

        ResetPassword resetPassword = event.getResetPassword();

        Account account = accountServiceImpl.findByEmail(resetPassword.getEmail());
        account.setPassword(resetPassword.getToken()); //TODO Уточнить откуда получать пароль
        log.info(account.getPassword());

        accountServiceImpl.update(account, account.getId());

    }

}
