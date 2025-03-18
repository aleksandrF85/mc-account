package com.example.mc_account.dto;

import com.example.mc_account.model.RoleType;
import com.example.mc_account.model.StatusCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Data
@NoArgsConstructor
public class AccountDto {

    private Long id;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private String token;
    private StatusCode statusCode;
    private String firstName;
    private String lastName;
    private OffsetDateTime regDate;
    private OffsetDateTime birthDate;
    private String messagePermission;
    private OffsetDateTime lastOnlineTime;
    private boolean isOnline;
    private boolean isBlocked;
    private boolean isDeleted;
    private String photoId;
    private String photoName;
    private RoleType role;
    private OffsetDateTime createdOn;
    private OffsetDateTime updatedOn;
    private String password;

}
