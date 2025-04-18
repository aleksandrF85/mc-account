package com.example.mc_account.services.impl;

import com.example.mc_account.event.ResetPassword;
import com.example.mc_account.event.ResetPasswordEvent;
import com.example.mc_account.event.UserRegistration;
import com.example.mc_account.event.UserRegistrationEvent;
import com.example.mc_account.model.Account;
import com.example.mc_account.services.AccountService;
import com.example.mc_account.services.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    @Value("${app.kafka.resetPassword}")
    private String resetPasswordTopic;

    @Value("${app.kafka.userRegistration")
    private String userRegistrationsTopic;

    @Value("${app.kafka.groupId}")
    private String groupId;

    private final AccountService accountServiceImpl;

    @KafkaListener(topics = "${app.kafka.userRegistrations}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "userRegistrationEventKafkaListenerContainerFactory")
    @Override
    public void createAccountByUserRegistrationEvent(UserRegistrationEvent event) {
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
