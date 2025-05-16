package com.example.mc_account.services;

import com.example.mc_account.events.*;


public interface KafkaService {

    void sendUserRegistrationEvent(AccountChangesEvent event);

    void sendNotificationEvent(NotificationEvent event);

    void createAccountByUserRegistrationEvent(UserRegistrationEvent event);

    void resetPassword(ResetPasswordEvent event);

    void changeEmail(ChangeEmailEvent event);

}
