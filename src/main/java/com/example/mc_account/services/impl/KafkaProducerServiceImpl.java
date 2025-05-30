package com.example.mc_account.services.impl;

import com.example.mc_account.events.AccountChangesEvent;
import com.example.mc_account.events.NotificationEvent;
import com.example.mc_account.services.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    @Value("${app.kafka.accountChanges}")
    private String accountChangesEventTopic;

    @Value("${app.kafka.notification}")
    private String notificationEventTopic;

    private final KafkaTemplate<String, AccountChangesEvent> accountChangesEventKafkaTemplate;
    private final KafkaTemplate<String, NotificationEvent> notificationEventKafkaTemplate;

    @Override
    public void sendAccountChangesEvent(AccountChangesEvent event) {
        CompletableFuture<SendResult<String, AccountChangesEvent>> result =
                accountChangesEventKafkaTemplate.send(accountChangesEventTopic, event);

        result.whenComplete((sendResult, ex) -> {
            if (ex != null) {
                log.error("Failed to send AccountChangesEvent: {}", event, ex);
            } else {
                log.info("Sent AccountChangesEvent: {}", event);
            }
        });
    }

    @Override
    public void sendNotificationEvent(NotificationEvent event) {
        CompletableFuture<SendResult<String, NotificationEvent>> result =
                notificationEventKafkaTemplate.send(notificationEventTopic, event);

        result.whenComplete((sendResult, ex) -> {
            if (ex != null) {
                log.error("Failed to send NotificationEvent: {}", event, ex);
            } else {
                log.info("Sent NotificationEvent: {}", event);
            }
        });
    }
}

