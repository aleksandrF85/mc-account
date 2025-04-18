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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private final AccountService accountServiceImpl;

    @KafkaListener(topics = "${app.kafka.userRegistration}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "userRegistrationEventKafkaListenerContainerFactory")
    @Override
    public void createAccountByUserRegistrationEvent(@Payload UserRegistrationEvent event,
                                                     @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                                     @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                                     @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {

        log.info("Received event: {}", event.getUserRegistration().toString());
        log.info("Key: {}; Partition: {}; Topic: {}; Timestamp: {}", key, partition, topic, timestamp);

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
