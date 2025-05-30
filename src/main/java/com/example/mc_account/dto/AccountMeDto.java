package com.example.mc_account.dto;

import com.example.mc_account.model.StatusCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AccountMeDto {

    private String id;
    @NotBlank(message = "Имя должно быть указано")
    @Size(min = 3, max = 20, message = "Имя не должно быть меньше {min} и больше {max} символов!")
    private String firstName;
    @NotBlank(message = "Фамилия должна быть указана")
    @Size(min = 3, max = 20, message = "Фамилия не должно быть меньше {min} и больше {max} символов!")
    private String lastName;
    @NotBlank(message = "Email должен быть указан")
    private String email;
    @NotBlank(message = "Пароль должен быть указан")
    @Size(min = 6, message = "Пароль должен быть не менее {min}")
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
    private boolean blocked;
    private boolean deleted;
    private boolean isOnline;

}