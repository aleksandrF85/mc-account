package com.example.mc_account.services;

import com.example.mc_account.event.ResetPasswordEvent;
import com.example.mc_account.event.UserRegistrationEvent;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.UUID;

public interface KafkaService {

    void createAccountByUserRegistrationEvent(String message);

    void resetPassword(ResetPasswordEvent event);
}
