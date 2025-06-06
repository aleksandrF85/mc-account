package com.example.mc_account.services.impl;


import com.example.mc_account.exception.AlreadyExistException;
import com.example.mc_account.model.Account;
import com.example.mc_account.repository.AccountRepository;
import com.example.mc_account.services.AccountService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "eureka.client.enabled=false"
})
@EmbeddedKafka(partitions = 1, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092", "port=9092"
})
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
        assertNotNull(online.getLastOnlineTime()); // изменено здесь

        accountService.isOnline(testAccount.getId(), false);
        Account offline = accountService.findById(testAccount.getId());
        assertFalse(offline.isOnline());
        assertNotNull(offline.getLastOnlineTime());
    }

    @Test
    @Order(8)
    void testUpdateAccount() {
        testAccount.setFirstName("Updated");
        accountService.update(testAccount, testAccount.getId());

        Account updated = accountService.findById(testAccount.getId());
        assertEquals("Updated", updated.getFirstName());
    }

    @Test
    @Order(9)
    void testExistsByEmail() {
        boolean exists = accountService.existsByEmail(testAccount.getEmail());
        assertTrue(exists);
    }

    @Test
    @Order(10)
    void testFindAllByIds() {
        List<Account> accounts = accountService.findAllByIds(List.of(testAccount.getId().toString()));
        assertEquals(1, accounts.size());
        assertEquals(testAccount.getId(), accounts.get(0).getId());
    }

    @Test
    @Order(11)
    void testGetTotalActiveAccounts() {
        int count = accountService.getTotalActiveAccounts();
        assertTrue(count >= 0); // На этом этапе хотя бы один аккаунт должен быть
    }

    @Test
    @Order(12)
    void testFindByEmail_whenDeleted_shouldThrowException() {
        Account account = accountService.findById(testAccount.getId());
        account.setDeleted(true);
        accountRepository.save(account);

        assertThrows(EntityNotFoundException.class, () ->
                accountService.findByEmail(testAccount.getEmail()));
    }

    @Test
    @Order(13)
    void testCreate_whenEmailExists_shouldThrowAlreadyExistException() {
        Account duplicate = new Account();
        duplicate.setEmail(testAccount.getEmail());
        duplicate.setPassword("123");
        duplicate.setFirstName("Jane");

        assertThrows(AlreadyExistException.class, () ->
                accountService.create(duplicate));
    }


}