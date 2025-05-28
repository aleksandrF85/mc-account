package com.example.mc_account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class McAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(McAccountApplication.class, args);
    }

}
