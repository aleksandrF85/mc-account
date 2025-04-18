package com.example.mc_account.services;

import com.example.mc_account.event.ResetPasswordEvent;
import com.example.mc_account.event.UserRegistrationEvent;

public interface KafkaService {

    void createAccountByUserRegistrationEvent(UserRegistrationEvent event);

    void resetPassword(ResetPasswordEvent event);
}
