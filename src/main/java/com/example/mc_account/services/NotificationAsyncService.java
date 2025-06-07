package com.example.mc_account.services;

import com.example.mc_account.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationAsyncService {
    private static final String LOG_PREFIX = "[BDAY]";

    private final AccountService accountServiceImpl;
    private final KafkaProducerService eventProducerService;
    private final AccountEventFactoryService eventFactoryService;
    private final Clock clock;

    @Async
    public void notifyFriendBirthdaysAsync(List<String> friendIds, UUID currentUserId) {
        log.info("{} Start checking birthdays for user {}", LOG_PREFIX, currentUserId);

        try {
            List<Account> friends = accountServiceImpl.findAllByIds(friendIds);
            OffsetDateTime now = OffsetDateTime.now(clock);

            if (friends.isEmpty()) {
                log.debug("{} No friends found for user {}", LOG_PREFIX, currentUserId);
                return;
            }

            String email = accountServiceImpl.findById(currentUserId).getEmail();
            int notificationsSent = processBirthdays(friends, now, currentUserId, email);

            log.info("{} Completed. User: {}, Friends checked: {}, Notifications sent: {}",
                    LOG_PREFIX, currentUserId, friends.size(), notificationsSent);

        } catch (Exception e) {
            log.error("{} Error processing birthdays for user {}: {}",
                    LOG_PREFIX, currentUserId, e.getMessage(), e);
        }
    }

    private int processBirthdays(List<Account> friends, OffsetDateTime now,
                                 UUID currentUserId, String email) {
        return (int) friends.stream()
                .filter(acc -> shouldNotify(acc, now))
                .peek(acc -> log.debug("{} Processing birthday for {} (birth date: {})",
                        LOG_PREFIX, acc.getId(), acc.getBirthDate()))
                .map(acc -> sendNotification(acc, currentUserId, email))
                .filter(success -> success)
                .count();
    }

    private boolean shouldNotify(Account account, OffsetDateTime now) {
        OffsetDateTime birthDate = account.getBirthDate();
        if (birthDate == null) {
            log.trace("{} No birth date for {}", LOG_PREFIX, account.getId());
            return false;
        }

        boolean isLeapYear = now.toLocalDate().isLeapYear();
        int birthMonth = birthDate.getMonthValue();
        int birthDay = birthDate.getDayOfMonth();

        // Обычные даты рождения
        if (birthMonth != 2 || birthDay != 29) {
            return birthDate.getMonth() == now.getMonth() &&
                    birthDay == now.getDayOfMonth();
        }

        // Обработка 29 февраля
        if (isLeapYear) {
            return now.getMonthValue() == 2 && now.getDayOfMonth() == 29;
        }

        // Для невисокосных годов
        return now.getMonthValue() == 3 && now.getDayOfMonth() == 1;
    }

    private boolean sendNotification(Account account, UUID currentUserId, String email) {
        try {
            var event = eventFactoryService.createBirthdayNotificationEvent(
                    account, currentUserId, email);
            eventProducerService.sendNotificationEvent(event);
            log.debug("{} Notification sent to {}", LOG_PREFIX, account.getId());
            return true;
        } catch (Exception e) {
            log.warn("{} Failed to send notification to {}: {}",
                    LOG_PREFIX, account.getId(), e.getMessage());
            return false;
        }
    }
}
