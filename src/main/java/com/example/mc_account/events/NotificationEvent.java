package com.example.mc_account.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private UUID id;
    private LocalDateTime sentTime;
    private UUID authorId;
    private UUID receiverId;
    private UUID eventId;
    private String content;
    private MicroServiceName serviceName;
    private NotificationType notificationType;
    private Boolean isReaded;
}
