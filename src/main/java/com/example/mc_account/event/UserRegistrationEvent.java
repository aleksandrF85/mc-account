package com.example.mc_account.event;


public class UserRegistrationEvent {
    private UserRegistration userRegistration;

    public UserRegistrationEvent() {
    }

    public UserRegistrationEvent(UserRegistration userRegistration) {
        this.userRegistration = userRegistration;
    }

    public UserRegistration getUserRegistration() {
        return userRegistration;
    }

    public void setUserRegistration(UserRegistration userRegistration) {
        this.userRegistration = userRegistration;
    }
}
