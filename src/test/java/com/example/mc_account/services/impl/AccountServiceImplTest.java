package com.example.mc_account.services.impl;

import com.example.mc_account.exception.AlreadyExistException;
import com.example.mc_account.model.Account;
import com.example.mc_account.reposirory.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(UUID.randomUUID());
        testAccount.setEmail("test@example.com");
    }

    @Test
    void create_whenEmailDoesNotExist_shouldSaveAccount() {
        when(accountRepository.existsByEmail(testAccount.getEmail())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account created = accountService.create(testAccount);

        assertEquals("test@example.com", created.getEmail());
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void create_whenEmailExists_shouldThrowException() {
        when(accountRepository.existsByEmail(testAccount.getEmail())).thenReturn(true);

        assertThrows(AlreadyExistException.class,
                () -> accountService.create(testAccount));
    }

    @Test
    void findById_whenExists_shouldReturnAccount() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

        Account found = accountService.findById(testAccount.getId());

        assertEquals(testAccount.getEmail(), found.getEmail());
    }

    @Test
    void deleteById_shouldMarkAccountAsDeleted() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

        accountService.deleteById(testAccount.getId());

        assertTrue(testAccount.isDeleted());
        verify(accountRepository).save(testAccount);
    }
}
