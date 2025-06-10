package com.example.mc_account.services.impl;

import com.example.mc_account.events.AccountChangesEvent;
import com.example.mc_account.events.NotificationEvent;
import com.example.mc_account.events.NotificationType;
import com.example.mc_account.events.MicroServiceName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KafkaProducerServiceImplTest {

    private KafkaTemplate<String, AccountChangesEvent> accountChangesKafkaTemplate;
    private KafkaTemplate<String, NotificationEvent> notificationKafkaTemplate;
    private KafkaProducerServiceImpl producerService;

    @BeforeEach
    void setUp() throws Exception {
        accountChangesKafkaTemplate = mock(KafkaTemplate.class);
        notificationKafkaTemplate = mock(KafkaTemplate.class);

        producerService = new KafkaProducerServiceImpl(accountChangesKafkaTemplate, notificationKafkaTemplate);

        // Устанавливаем приватные поля через reflection
        setField(producerService, "accountChangesEventTopic", "test-account-changes-topic");
        setField(producerService, "notificationEventTopic", "test-notification-topic");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void sendAccountChangesEvent_shouldSendMessage() {
        AccountChangesEvent event = new AccountChangesEvent(); // заполни нужными полями
        CompletableFuture<SendResult<String, AccountChangesEvent>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(accountChangesKafkaTemplate.send(anyString(), eq(event))).thenReturn(future);

        producerService.sendAccountChangesEvent(event);

        verify(accountChangesKafkaTemplate, timeout(500).times(1))
                .send("test-account-changes-topic", event);
    }

    @Test
    void sendNotificationEvent_shouldSendMessage() {
        NotificationEvent event = new NotificationEvent();
        event.setId(UUID.randomUUID());
        event.setEventId(UUID.randomUUID());
        event.setAuthorId(UUID.randomUUID());
        event.setReceiverId(UUID.randomUUID());
        event.setNotificationType(NotificationType.FRIEND_BIRTHDAY);
        event.setServiceName(MicroServiceName.MC_ACCOUNT);
        event.setSentTime(OffsetDateTime.now());
        event.setEmail("test@example.com");
        event.setContent("У пользователя Иван Иванов сегодня день рождения!");
        event.setIsReaded(false);

        CompletableFuture<SendResult<String, NotificationEvent>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(notificationKafkaTemplate.send(anyString(), eq(event))).thenReturn(future);

        producerService.sendNotificationEvent(event);

        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationKafkaTemplate, timeout(500).times(1))
                .send(eq("test-notification-topic"), eventCaptor.capture());

        NotificationEvent captured = eventCaptor.getValue();

        assertThat(captured.getEmail()).isEqualTo(event.getEmail());
        assertThat(captured.getContent()).contains("день рождения");
        assertThat(captured.getNotificationType()).isEqualTo(NotificationType.FRIEND_BIRTHDAY);
        assertThat(captured.getServiceName()).isEqualTo(MicroServiceName.MC_ACCOUNT);
    }
}
