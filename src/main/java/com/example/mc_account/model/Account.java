package com.example.mc_account.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "account")
public class Account {

    @Id
    @Column(name = "id",unique = true, nullable = false)
    private UUID id;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection(targetClass = RoleType.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "role", nullable = false)
    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    private Set<RoleType> role = new HashSet<>();

    @Column(name = "phone")
    private String phone;

    @Column(name = "photo")
    private String photo;

    @Column(name = "profileCover")
    private String profileCover;

    @Column(name = "about")
    private String about;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusCode")
    private StatusCode statusCode;

    @Column(name = "regDate")
    private LocalDateTime regDate;

    @Column(name = "birthDate")
    private LocalDateTime birthDate;

    @Column(name = "messagePermission")
    private String messagePermission;

    @Column(name = "lastOnlineTime")
    private LocalDateTime lastOnlineTime;


    @Column(name = "emojiStatus")
    private String emojiStatus;

    @Column(name = "createdOn")
    @CreationTimestamp
    private LocalDateTime createdOn;

    @Column(name = "updatedOn")
    @UpdateTimestamp
    private LocalDateTime updatedOn;

    @Column(name = "deletionTimestamp")
    private LocalDateTime deletionTimestamp;

    @Column(name = "isDeleted")
    private boolean deleted;
    @Column(name = "isBlocked")
    private boolean blocked;

    @Column(name = "isOnline")
    private boolean isOnline;

}
