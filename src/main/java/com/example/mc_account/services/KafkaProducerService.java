package com.example.mc_account.services;

import com.example.mc_account.events.AccountChangesEvent;
import com.example.mc_account.events.NotificationEvent;

public interface KafkaProducerService {

    void sendAccountChangesEvent(AccountChangesEvent event);

    void sendNotificationEvent(NotificationEvent event);
}
