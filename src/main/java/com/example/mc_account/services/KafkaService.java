package com.example.mc_account.services;

import com.skillbox.auth.dto.events.ResetPasswordEvent;
import com.skillbox.auth.dto.events.UserRegistrationEvent;

public interface KafkaService {

    void createAccountByUserRegistrationEvent(UserRegistrationEvent event);

    void resetPassword(ResetPasswordEvent event);
}
