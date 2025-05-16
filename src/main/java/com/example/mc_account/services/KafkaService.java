package com.example.mc_account.services;

import com.example.mc_account.events.AccountChangesEvent;
import com.example.mc_account.events.ResetPasswordEvent;
import com.example.mc_account.events.UserRegistrationEvent;
import com.example.mc_account.events.ChangeEmailEvent;


public interface KafkaService {

    void sendUserRegistrationEvent(AccountChangesEvent event);

    void createAccountByUserRegistrationEvent(UserRegistrationEvent event);

    void resetPassword(ResetPasswordEvent event);

    void changeEmail(ChangeEmailEvent event);

}
