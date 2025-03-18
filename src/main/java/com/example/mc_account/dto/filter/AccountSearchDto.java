package com.example.mc_account.dto.filter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
@Data
@NoArgsConstructor
public class AccountSearchDto {

    private List<Long> ids;
    private String author;
    private String firstName;
    private String lastName;
    private OffsetDateTime birthDateFrom;
    private OffsetDateTime birthDateTo;
    private String city;
    private String country;
    private boolean isBlocked;
    private boolean isDeleted;
    private int ageTo;
    private int ageFrom;
}
