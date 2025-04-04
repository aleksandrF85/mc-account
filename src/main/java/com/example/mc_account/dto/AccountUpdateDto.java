package com.example.mc_account.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AccountUpdateDto {
    private String firstName;
    private String lastName;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private LocalDateTime birthDate;
    private String emojiStatus;
}