package com.example.mc_account.dto;

import com.example.mc_account.model.RoleType;
import com.example.mc_account.model.StatusCode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;


@Data
public class AccountResponseDto {
    private final Set<RoleType> role;
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String photo;
    private String profileCover;
    private String about;
    private String city;
    private String country;
    private StatusCode statusCode;
    private LocalDateTime regDate;
    private LocalDateTime birthDate;
    private String messagePermission;
    private LocalDateTime lastOnlineTime;
    private String emojiStatus;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private LocalDateTime deletionTimestamp;
    private boolean deleted;
    private boolean blocked;
    private boolean isOnline;
}