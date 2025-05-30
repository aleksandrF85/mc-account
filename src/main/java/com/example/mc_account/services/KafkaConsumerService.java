package com.example.mc_account.services;

import com.example.mc_account.events.ChangeEmailEvent;
import com.example.mc_account.events.ResetPasswordEvent;
import com.example.mc_account.events.UserRegistrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {

    @Autowired
    private AccountEventHandlerService eventHandlerService;

    public KafkaConsumerService(AccountEventHandlerService eventHandlerService) {
        this.eventHandlerService = eventHandlerService;
    }

    @KafkaListener(topics = "${app.kafka.userRegistration}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "userRegistrationEventKafkaListenerContainerFactory")
    public void createAccountByUserRegistrationEvent(UserRegistrationEvent event) {
        log.info("Received event: {}", event);
        eventHandlerService.handleUserRegistrationEvent(event);
    }

    @KafkaListener(topics = "${app.kafka.resetPassword}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "resetPasswordEventKafkaListenerContainerFactory")
    public void resetPassword(ResetPasswordEvent event) {
        log.info("Received event: {}", event);
        eventHandlerService.handleResetPasswordEvent(event);
    }

    @KafkaListener(topics = "${app.kafka.emailChange}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "changeEmailEventKafkaListenerContainerFactory")
    public void changeEmail(ChangeEmailEvent event) {
        log.info("Received event: {}", event);
        eventHandlerService.handleChangeEmailEvent(event);
    }
}

