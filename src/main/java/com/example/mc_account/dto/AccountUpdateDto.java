package com.example.mc_account.dto;

import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class AccountUpdateDto{
    private String firstName;
    private String lastName;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private OffsetDateTime birthDate;
    private String emojiStatus;
}