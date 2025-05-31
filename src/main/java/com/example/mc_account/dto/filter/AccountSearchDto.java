package com.example.mc_account.dto.filter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class AccountSearchDto {

    private UUID currentUserId;
    private String author;
    private List<String> ids;
    private String firstName;
    private String lastName;
    private int ageTo;
    private int ageFrom;
    private String country;
    private String city;
    private boolean isDeleted;

}
