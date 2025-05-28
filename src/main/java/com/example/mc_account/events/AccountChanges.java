package com.example.mc_account.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountChanges {

    private String id;
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
