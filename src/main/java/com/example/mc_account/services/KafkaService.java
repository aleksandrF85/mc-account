package com.example.mc_account.services;

import com.example.mc_account.event.ResetPasswordEvent;
import com.example.mc_account.event.UserRegistrationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.UUID;

public interface KafkaService {

    @KafkaListener(topics = "${app.kafka.userRegistration}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "userRegistrationEventKafkaListenerContainerFactory")
    void createAccountByUserRegistrationEvent(@Payload UserRegistrationEvent event,
                                              @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                              @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                              @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp);

    @KafkaListener(topics = "${app.kafka.resetPassword}",
            groupId = "${app.kafka.groupId}",
            containerFactory = "resetPasswordEventKafkaListenerContainerFactory")
    void resetPassword(ResetPasswordEvent event);
}
