package com.example.mc_account.services.impl;

import com.example.mc_account.events.*;
import com.example.mc_account.mapper.AccountMapper;
import com.example.mc_account.model.Account;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.services.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
//@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    @Value("${app.kafka.accountChanges}")
    private String accountChangesEventTopic;

    @Value("${app.kafka.notification}")
    private String notificationEventTopic;
    @Autowired
    private AccountService accountServiceImpl;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private KafkaTemplate<String, AccountChangesEvent> accountChangesEventKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, NotificationEvent> notificationEventKafkaTemplate;

    @Override
    public void sendUserRegistrationEvent(AccountChangesEvent event) {

        CompletableFuture<SendResult<String, AccountChangesEvent>> result =
                accountChangesEventKafkaTemplate.send(accountChangesEventTopic, event);

        log.info("Sent event {}", event);

   }

    @Override
    public void sendNotificationEvent(NotificationEvent event) {

        CompletableFuture<SendResult<String, NotificationEvent>> result =
                notificationEventKafkaTemplate.send(notificationEventTopic, event);

        log.info("Sent event {}", event);

    }

    @KafkaListener(topics = "${app.kafka.userRegistration}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "userRegistrationEventKafkaListenerContainerFactory")
    @Override
    public void createAccountByUserRegistrationEvent(UserRegistrationEvent event) {


        log.info("Received event: {}", event.toString());

        Account account = accountMapper.userRegistrationToAccount(event.getUserRegistration());

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
