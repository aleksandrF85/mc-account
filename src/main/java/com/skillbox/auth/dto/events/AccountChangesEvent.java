package com.skillbox.auth.dto.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountChangesEvent {

    private AccountChanges accountChanges;
}
