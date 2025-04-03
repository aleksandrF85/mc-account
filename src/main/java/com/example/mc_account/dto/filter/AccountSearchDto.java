package com.example.mc_account.dto.filter;

import com.example.mc_account.model.StatusCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AccountSearchDto {

    private String author;
    private List<Long> ids;
    private String firstName;
    private String lastName;
    private int ageTo;
    private int ageFrom;
    private String country;
    private String city;
    private StatusCode statusCode;
    private boolean isDeleted;

}
