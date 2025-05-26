package com.example.mc_account.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);       // Минимальное число потоков
        executor.setMaxPoolSize(10);       // Максимальное число потоков
        executor.setQueueCapacity(25);     // Размер очереди задач
        executor.setThreadNamePrefix("AsyncExecutor-");

        executor.initialize();
        return executor;
    }
}
