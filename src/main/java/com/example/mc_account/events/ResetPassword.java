package com.example.mc_account.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPassword {
    private String token;
    private String email;
    private Instant expirationTime;

}
