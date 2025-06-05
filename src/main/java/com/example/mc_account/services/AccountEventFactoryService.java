package com.example.mc_account.services;

import com.example.mc_account.dto.AccountUpdateDto;
import com.example.mc_account.events.*;
import com.example.mc_account.model.Account;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AccountEventFactoryService {

    public AccountChangesEvent toAccountChangesEvent(AccountUpdateDto dto, String accountId) {
        return new AccountChangesEvent(
                new AccountChanges(
                        accountId,
                        dto.getFirstName(),
                        dto.getLastName(),
                        dto.getPhone(),
                        dto.getPhoto(),
                        dto.getAbout(),
                        dto.getCity(),
                        dto.getCountry(),
                        dto.getBirthDate().toLocalDateTime(),
                        dto.getEmojiStatus()
                )
        );
    }

    public NotificationEvent createBirthdayNotificationEvent(Account birthdayPerson, UUID receiverId, String email) {
        return NotificationEvent.builder()
                .eventId(UUID.randomUUID())
                .id(birthdayPerson.getId())
                .receiverId(receiverId)
                .notificationType(NotificationType.FRIEND_BIRTHDAY)
                .serviceName(MicroServiceName.MC_ACCOUNT)
                .sentTime(OffsetDateTime.now().toLocalDateTime())
                .email(email)
                .content(String.format(
                        "У пользователя %s %s сегодня день рождения!",
                        birthdayPerson.getFirstName(),
                        birthdayPerson.getLastName()
                ))
                .build();
    }

}
