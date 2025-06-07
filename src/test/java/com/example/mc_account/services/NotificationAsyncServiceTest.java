package com.example.mc_account.services;

import com.example.mc_account.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class NotificationAsyncServiceTest {
    @Mock
    protected AccountService accountService;
    @Mock
    protected KafkaProducerService producerService;
    @Mock
    protected AccountEventFactoryService factoryService;

    protected Clock testClock;
    protected NotificationAsyncService notificationAsyncService;
    protected final UUID currentUserId = UUID.randomUUID();
    protected final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        testClock = Clock.systemDefaultZone();
        notificationAsyncService = new NotificationAsyncService(
                accountService,
                producerService,
                factoryService,
                testClock
        );

        Mockito.when(accountService.findById(currentUserId))
                .thenReturn(createAccount(currentUserId, email, null));
    }

    protected Account createAccount(UUID id, String email, OffsetDateTime birthDate) {
        Account acc = new Account();
        acc.setId(id);
        acc.setEmail(email);
        acc.setBirthDate(birthDate);
        return acc;
    }


    @Test
    void shouldNotSendNotification_WhenBirthDateIsNull() {
        List<Account> friends = List.of(createAccount(UUID.randomUUID(), "friend1@example.com", null));
        Mockito.when(accountService.findAllByIds(Mockito.anyList())).thenReturn(friends);

        notificationAsyncService.notifyFriendBirthdaysAsync(List.of("1"), currentUserId);

        Mockito.verify(producerService, Mockito.never()).sendNotificationEvent(Mockito.any());
    }

    @Test
    void shouldSendNotification_ForLeapYearBirthdayOnLeapYear() {
        // Устанавливаем фиксированную дату - 29 февраля 2024
        LocalDate leapDay = LocalDate.of(2024, 2, 29);
        testClock = Clock.fixed(leapDay.atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

        notificationAsyncService = new NotificationAsyncService(
                accountService,
                producerService,
                factoryService,
                testClock
        );

        Account leapFriend = createAccount(
                UUID.randomUUID(),
                "leap@example.com",
                leapDay.atStartOfDay().atOffset(ZoneOffset.UTC)
        );

        Mockito.when(accountService.findAllByIds(Mockito.anyList()))
                .thenReturn(List.of(leapFriend));

        notificationAsyncService.notifyFriendBirthdaysAsync(List.of("1"), currentUserId);

        Mockito.verify(producerService, Mockito.times(1))
                .sendNotificationEvent(Mockito.any());
    }

    @Test
    void shouldSendNotification_ForLeapYearBirthdayOnNotLeapYear() {
        // Устанавливаем дату 1 марта 2023 (не високосный год)
        LocalDate march1st = LocalDate.of(2023, 3, 1);
        testClock = Clock.fixed(march1st.atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

        notificationAsyncService = new NotificationAsyncService(
                accountService,
                producerService,
                factoryService,
                testClock
        );

        // Друг с ДР 29 февраля
        LocalDate leapBirthday = LocalDate.of(2000, 2, 29); // Високосный год рождения
        Account leapFriend = createAccount(
                UUID.randomUUID(),
                "leap@example.com",
                leapBirthday.atStartOfDay().atOffset(ZoneOffset.UTC)
        );

        Mockito.when(accountService.findAllByIds(Mockito.anyList()))
                .thenReturn(List.of(leapFriend));

        notificationAsyncService.notifyFriendBirthdaysAsync(List.of("1"), currentUserId);

        // Проверяем, что уведомление было отправлено
        Mockito.verify(producerService, Mockito.times(1))
                .sendNotificationEvent(Mockito.any());

        // Дополнительно можно проверить, что это именно уведомление для 29 февраля
        Mockito.verify(factoryService).createBirthdayNotificationEvent(
                Mockito.eq(leapFriend),
                Mockito.eq(currentUserId),
                Mockito.eq(email)
        );
    }

    @Test
    void shouldSendNotification_WhenBirthdayIsToday() {
        OffsetDateTime today = OffsetDateTime.now(testClock);
        Account birthdayFriend = createAccount(
                UUID.randomUUID(),
                "birthday@example.com",
                today
        );

        Mockito.when(accountService.findAllByIds(Mockito.anyList()))
                .thenReturn(List.of(birthdayFriend));

        notificationAsyncService.notifyFriendBirthdaysAsync(List.of("1"), currentUserId);

        Mockito.verify(producerService, Mockito.timeout(1000).atLeastOnce())
                .sendNotificationEvent(Mockito.any());
    }

    @Test
    void shouldNotSendNotification_WhenBirthdayIsNotToday() {
        OffsetDateTime birthday = OffsetDateTime.now().withMonth(1).withDayOfMonth(1);
        Account notTodayFriend = createAccount(UUID.randomUUID(), "notoday@example.com", birthday);
        Mockito.when(accountService.findAllByIds(Mockito.anyList())).thenReturn(List.of(notTodayFriend));

        notificationAsyncService.notifyFriendBirthdaysAsync(List.of("1"), currentUserId);

        Mockito.verify(producerService, Mockito.never()).sendNotificationEvent(Mockito.any());
    }
}


