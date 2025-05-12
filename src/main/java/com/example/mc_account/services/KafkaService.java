package com.example.mc_account.services;

import com.skillbox.auth.dto.events.AccountChangesEvent;
import com.skillbox.auth.dto.events.ResetPasswordEvent;
import com.skillbox.auth.dto.events.UserRegistrationEvent;
import com.skillbox.auth.dto.events.ChangeEmailEvent;


public interface KafkaService {

    void sendUserRegistrationEvent(AccountChangesEvent event);

    void createAccountByUserRegistrationEvent(UserRegistrationEvent event);

    void resetPassword(ResetPasswordEvent event);

    void changeEmail(ChangeEmailEvent event);

}
