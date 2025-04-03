package com.example.mc_account.dto;

import com.example.mc_account.model.RoleType;
import com.example.mc_account.model.StatusCode;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;


@Data
public class AccountResponseDto {
    private final Set<RoleType> role;
    private UUID id;
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
    private OffsetDateTime regDate;
    private OffsetDateTime birthDate;
    private String messagePermission;
    private OffsetDateTime lastOnlineTime;
    private String emojiStatus;
    private OffsetDateTime createdOn;
    private OffsetDateTime updatedOn;
    private OffsetDateTime deletionTimestamp;
    private boolean deleted;
    private boolean blocked;
    private boolean isOnline;
}