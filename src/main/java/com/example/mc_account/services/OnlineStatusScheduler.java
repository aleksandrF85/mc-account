package com.example.mc_account.services;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class OnlineStatusScheduler {

    @Autowired
    private AccountService accountService;

    @Async("taskExecutor")
    public void scheduleOffline(@Nonnull UUID id, @Nonnull Duration delay) {
        log.info("[ASYNC] Scheduling user {} to go offline in {} ms", id, delay.toMillis());

        try {
            Thread.sleep(delay.toMillis());
            accountService.isOnline(id, false);
            log.info("[ASYNC] User {} marked as offline after delay", id);
        } catch (InterruptedException e) {
            log.warn("[ASYNC] Interrupted while scheduling offline status for user {}", id);
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            log.error("[ASYNC] Error while marking user {} as offline", id, ex);
        }
    }
}