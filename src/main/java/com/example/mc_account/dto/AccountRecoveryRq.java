package com.example.mc_account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountRecoveryRq {

    private String email;

    private String password;
}
