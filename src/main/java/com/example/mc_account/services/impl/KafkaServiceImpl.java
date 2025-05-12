package com.example.mc_account.services.impl;

import com.example.mc_account.model.RoleType;
import com.example.mc_account.model.Account;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.services.KafkaService;
import com.skillbox.auth.dto.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    @Value("${app.kafka.accountChanges}")
    private String accountChangesEventTopic;
    private final AccountService accountServiceImpl;
    private final KafkaTemplate<String, AccountChangesEvent> template;

    @Override
    public void sendUserRegistrationEvent(AccountChangesEvent event) {

        CompletableFuture<SendResult<String, AccountChangesEvent>> result = template.send(accountChangesEventTopic, event);

        log.info("Sent event {}", event);

   }

    @KafkaListener(topics = "${app.kafka.userRegistration}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "userRegistrationEventKafkaListenerContainerFactory")
    @Override
    public void createAccountByUserRegistrationEvent(UserRegistrationEvent event) {


        log.info("Received event: {}", event.toString());

        Account account = new Account();
        UserRegistration userRegistration = event.getUserRegistration();

        account.setEmail(userRegistration.getEmail());
        account.setFirstName(userRegistration.getFirstName());
        account.setLastName(userRegistration.getLastName());
        account.setId(UUID.fromString(userRegistration.getUserId()));
        account.setPassword(userRegistration.getPassword());
        account.setRole(Set.of(Enum.valueOf(RoleType.class, userRegistration.getRole())));
        account.setRegDate(LocalDateTime.now());
        account.setDeleted(false);
        account.setBlocked(false);
        account.setOnline(false);

        accountServiceImpl.create(account);
        log.info("Account created: " + account);
    }

    @KafkaListener(topics = "${app.kafka.resetPassword}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "resetPasswordEventKafkaListenerContainerFactory")
    @Override
    public void resetPassword(ResetPasswordEvent event) {

        log.info("Received event: {}", event.toString());

        ResetPassword resetPassword = event.getResetPassword();

        Account account = accountServiceImpl.findByEmail(resetPassword.getEmail());
        account.setPassword(resetPassword.getToken()); //TODO Уточнить откуда получать новый пароль

        accountServiceImpl.update(account, account.getId());
        log.info("Account updated: " + account);

    }

    @KafkaListener(topics = "${app.kafka.emailChange}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "changeEmailEventKafkaListenerContainerFactory")
    @Override
    public void changeEmail(ChangeEmailEvent event) {

        log.info("Received event: {}", event.toString());

        ChangeEmail changeEmail = event.getChangeEmail();

        Account account = accountServiceImpl.findById(UUID.fromString(changeEmail.getUserId()));
        account.setEmail(changeEmail.getEmail());

        accountServiceImpl.update(account, account.getId());
        log.info("Account updated: " + account);

    }


}
