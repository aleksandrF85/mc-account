package com.example.mc_account.services;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsWebClientService {

    private final WebClient.Builder webClientBuilder;


    public List<String> getFriendsIds(String bearerToken) {
        return webClientBuilder.build()
                .get()
                .uri("lb://mc-friends/api/v1/friends/friendId")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()

                // Обработка ошибок HTTP уровня (5xx)
                .onStatus(
                        status -> status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("Ошибка сервиса mc-friends (5xx): " + errorBody);
                                    return Mono.error(new RuntimeException("Ошибка mc-friends: " + clientResponse.statusCode()));
                                })
                )

                // Преобразуем тело в List<String>
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })

                // Ошибка при недоступности сервиса (например, DNS, таймаут)
                .onErrorResume(WebClientRequestException.class, ex -> {
                    System.err.println("Сервис mc-friends недоступен: " + ex.getMessage());
                    return Mono.just(Collections.emptyList());
                })

                // Общая обработка любых других исключений
                .onErrorResume(Exception.class, ex -> {
                    System.err.println("Неизвестная ошибка при вызове mc-friends: " + ex.getMessage());
                    return Mono.just(Collections.emptyList());
                })

                // Блокируем для получения результата (если нужен синхронный вызов)
                .block();
    }

    public List<String> getIdsByStatusCode(String bearerToken, String statusCode) {
        return webClientBuilder.build()
                .get()
                .uri("lb://mc-friends/api/v1/friends/status/{status}", statusCode)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()

                // Обработка ошибок HTTP уровня (5xx)
                .onStatus(
                        status -> status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("Ошибка сервиса mc-friends (5xx): " + errorBody);
                                    return Mono.error(new RuntimeException("Ошибка mc-friends: " + clientResponse.statusCode()));
                                })
                )

                // Преобразуем тело в List<String>
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })

                // Ошибка при недоступности сервиса (например, DNS, таймаут)
                .onErrorResume(WebClientRequestException.class, ex -> {
                    System.err.println("Сервис mc-friends недоступен: " + ex.getMessage());
                    return Mono.just(Collections.emptyList());
                })

                // Общая обработка любых других исключений
                .onErrorResume(Exception.class, ex -> {
                    System.err.println("Неизвестная ошибка при вызове mc-friends: " + ex.getMessage());
                    return Mono.just(Collections.emptyList());
                })

                // Блокируем для получения результата (если нужен синхронный вызов)
                .block();
    }

}
