package com.example.mc_account.services;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class OnlineStatusScheduler {

    @Autowired
    private AccountService accountService;

    @Async("taskExecutor")
    public void scheduleOffline(@Nonnull UUID id, @Nonnull Duration delay) {
        try {
            Thread.sleep(delay.toMillis());
            accountService.isOnline(id, false);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}