package com.skillbox.auth.dto.events;

import java.time.Instant;

public class ResetPassword {
    private String token;
    private String email;
    private Instant expirationTime;

    public ResetPassword() {
    }

    public ResetPassword(String token, String email, Instant expirationTime) {
        this.token = token;
        this.email = email;
        this.expirationTime = expirationTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }
}
