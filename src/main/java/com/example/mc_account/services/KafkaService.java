package com.example.mc_account.services;

import com.example.mc_account.event.ResetPasswordEvent;
import com.example.mc_account.event.UserRegistrationEvent;
import org.springframework.kafka.annotation.KafkaListener;

public interface KafkaService {
    @KafkaListener(topics = "${app.kafka.userRegistrations}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "userRegistrationEventKafkaListenerContainerFactory")
    void createAccountByUserRegistrationEvent(UserRegistrationEvent event);

    @KafkaListener(topics = "${app.kafka.resetPassword}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "resetPasswordEventKafkaListenerContainerFactory")
    void resetPassword(ResetPasswordEvent event);
}
