package com.example.mc_account.services;

import com.example.mc_account.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationAsyncService {

    private final AccountService accountServiceImpl;
    private final KafkaProducerService eventProducerService;
    private final AccountEventFactoryService eventFactoryService;

    @Async
    public void notifyFriendBirthdaysAsync(List<String> friendIds, UUID currentUserId) {
        log.info("[ASYNC] Start notifying birthdays for user {}", currentUserId);

        List<Account> friends = accountServiceImpl.findAllByIds(friendIds);
        OffsetDateTime now = OffsetDateTime.now();

        String email = accountServiceImpl.findById(currentUserId).getEmail();

        friends.stream()
                .filter(acc -> acc.getBirthDate() != null &&
                        acc.getBirthDate().getMonth() == now.getMonth() &&
                        acc.getBirthDate().getDayOfMonth() == now.getDayOfMonth())
                .forEach(acc -> {
                    try {
                        eventProducerService.sendNotificationEvent(
                                eventFactoryService.createBirthdayNotificationEvent(acc, currentUserId, email)
                        );
                        log.info("[ASYNC] Sent birthday notification to {}", acc.getId());
                    } catch (Exception e) {
                        log.warn("[ASYNC] Failed to send birthday notification to {}", acc.getId(), e);
                    }
                });

        log.info("[ASYNC] Finished birthday notifications for user {}", currentUserId);
    }
}
