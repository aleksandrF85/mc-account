package com.example.mc_account.services;

import com.example.mc_account.events.ChangeEmailEvent;
import com.example.mc_account.events.ResetPasswordEvent;
import com.example.mc_account.events.UserRegistrationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;

class KafkaConsumerServiceTest {

    @Mock
    private AccountEventHandlerService eventHandlerService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccountByUserRegistration_shouldCallHandler() {
        UserRegistrationEvent event = mock(UserRegistrationEvent.class);

        kafkaConsumerService.createAccountByUserRegistration(event);

        verify(eventHandlerService, times(1)).handleUserRegistrationEvent(event);
    }

    @Test
    void resetPassword_shouldCallHandler() {
        ResetPasswordEvent event = mock(ResetPasswordEvent.class);

        kafkaConsumerService.resetPassword(event);

        verify(eventHandlerService, times(1)).handleResetPasswordEvent(event);
    }

    @Test
    void changeEmail_shouldCallHandler() {
        ChangeEmailEvent event = mock(ChangeEmailEvent.class);

        kafkaConsumerService.changeEmail(event);

        verify(eventHandlerService, times(1)).handleChangeEmailEvent(event);
    }
}
