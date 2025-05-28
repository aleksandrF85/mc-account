package com.example.mc_account.services.impl;


import com.example.mc_account.model.Account;
import com.example.mc_account.reposirory.AccountRepository;
import com.example.mc_account.services.AccountService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountServiceImplIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    private Account testAccount;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void init() {
        testAccount = new Account();
        testAccount.setId(UUID.fromString("d8ed7e6d-0c4f-48dc-b7df-ed5f2b4cee37"));
        testAccount.setEmail("test@example.com");
        testAccount.setPassword("123456");
        testAccount.setFirstName("John");
        testAccount.setLastName("Doe");
        testAccount.setCountry("USA");
        testAccount.setCity("New York");
        testAccount.setDeleted(false);
        testAccount.setBlocked(false);
        testAccount.setOnline(false);
    }

    @Test
    @Order(1)
    void testCreateAccount() {
        Account created = accountService.create(testAccount);
        assertNotNull(created.getId());
        assertEquals(testAccount.getEmail(), created.getEmail());
    }

    @Test
    @Order(2)
    void testFindByEmail() {
        Account found = accountService.findByEmail(testAccount.getEmail());
        assertEquals(testAccount.getEmail(), found.getEmail());
    }

    @Test
    @Order(3)
    void testFindById() {
        Account found = accountService.findById(testAccount.getId());
        assertEquals(testAccount.getId(), found.getId());
    }

    @Test
    @Order(4)
    void testFindAll() {
        List<Account> accounts = accountService.findAll();
        assertFalse(accounts.isEmpty());
    }

    @Test
    @Order(5)
    void testDeleteById() {
        accountService.deleteById(testAccount.getId());
        Account deleted = accountService.findById(testAccount.getId());
        assertTrue(deleted.isDeleted());
        assertNotNull(deleted.getDeletionTimestamp());
    }

    @Test
    @Order(6)
    void testBlockById() {
        accountService.blockById(testAccount.getId());
        Account blocked = accountService.findById(testAccount.getId());
        assertTrue(blocked.isBlocked());
    }

    @Test
    @Order(7)
    void testIsOnline() {
        accountService.isOnline(testAccount.getId(), true);
        Account online = accountService.findById(testAccount.getId());
        assertTrue(online.isOnline());
        assertNull(online.getLastOnlineTime());

        accountService.isOnline(testAccount.getId(), false);
        Account offline = accountService.findById(testAccount.getId());
        assertFalse(offline.isOnline());
        assertNotNull(offline.getLastOnlineTime());
    }

}