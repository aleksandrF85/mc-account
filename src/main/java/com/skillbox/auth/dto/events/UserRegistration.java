package com.skillbox.auth.dto.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistration {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String role;

}
