package com.example.mc_account.services;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsWebClientService {

    private final WebClient.Builder webClientBuilder;


    public List<String> getFriendsIds(String bearerToken) {
        return webClientBuilder.build()
                .get()
                .uri("http://mc-friends/api/friends/friendId")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }
    public List<String> getIdsByStatusCode(String bearerToken, String statusCode) {
        return webClientBuilder.build()
                .get()
                .uri("http://mc-friends/api/friends/status/{status}",
                        statusCode)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }
}
