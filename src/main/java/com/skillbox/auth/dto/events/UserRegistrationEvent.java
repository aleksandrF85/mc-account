package com.skillbox.auth.dto.events;


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

    @Override
    public String toString() {
        return "UserRegistrationEvent{" +
                "userRegistration=" + userRegistration +
                '}';
    }
}
