package com.example.mc_account.services.impl;

import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.exception.AlreadyExistException;
import com.example.mc_account.model.Account;
import com.example.mc_account.repository.AccountRepository;
import com.example.mc_account.services.OnlineStatusScheduler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OnlineStatusScheduler onlineStatusScheduler;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(UUID.randomUUID());
        testAccount.setEmail("test@example.com");
        testAccount.setDeleted(false);
        testAccount.setBlocked(false);
    }

    @Test
    void create_whenEmailDoesNotExist_shouldSaveAccount() {
        when(accountRepository.existsByEmail(testAccount.getEmail())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account created = accountService.create(testAccount);

        assertEquals("test@example.com", created.getEmail());
        verify(accountRepository).save(testAccount);
    }

    @Test
    void create_whenEmailExists_shouldThrowException() {
        when(accountRepository.existsByEmail(testAccount.getEmail())).thenReturn(true);
        assertThrows(AlreadyExistException.class, () -> accountService.create(testAccount));
    }

    @Test
    void findById_whenExists_shouldReturnAccount() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        Account found = accountService.findById(testAccount.getId());
        assertEquals(testAccount.getEmail(), found.getEmail());
    }

    @Test
    void findById_whenNotFound_shouldThrowException() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.findById(id));
    }

    @Test
    void deleteById_shouldMarkAccountAsDeleted() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        accountService.deleteById(testAccount.getId());
        assertTrue(testAccount.isDeleted());
        assertNotNull(testAccount.getDeletionTimestamp());
        verify(accountRepository).save(testAccount);
    }

    @Test
    void blockById_shouldBlockAccount() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        accountService.blockById(testAccount.getId());
        assertTrue(testAccount.isBlocked());
        verify(accountRepository).save(testAccount);
    }

    @Test
    void isOnline_shouldUpdateStatusAndScheduleOffline() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        accountService.isOnline(testAccount.getId(), true);
        assertTrue(testAccount.isOnline());
        assertNotNull(testAccount.getLastOnlineTime());
        verify(accountRepository).save(testAccount);
        verify(onlineStatusScheduler).scheduleOffline(eq(testAccount.getId()), any());
    }

    @Test
    void existsByEmail_shouldReturnCorrectValue() {
        when(accountRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertTrue(accountService.existsByEmail("test@example.com"));
    }

    @Test
    void findByEmail_shouldReturnAccount() {
        when(accountRepository.findByEmail(testAccount.getEmail())).thenReturn(Optional.of(testAccount));
        Account found = accountService.findByEmail(testAccount.getEmail());
        assertEquals(testAccount.getEmail(), found.getEmail());
    }

    @Test
    void findByEmail_whenDeleted_shouldThrowException() {
        testAccount.setDeleted(true);
        when(accountRepository.findByEmail(testAccount.getEmail())).thenReturn(Optional.of(testAccount));
        assertThrows(EntityNotFoundException.class, () -> accountService.findByEmail(testAccount.getEmail()));
    }

    @Test
    void findByEmail_whenNotFound_shouldThrowException() {
        when(accountRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.findByEmail("notfound@example.com"));
    }

    @Test
    void findAllByIds_shouldReturnAccounts() {
        List<String> ids = List.of(testAccount.getId().toString());
        when(accountRepository.findAllById(anyList())).thenReturn(List.of(testAccount));
        List<Account> result = accountService.findAllByIds(ids);
        assertEquals(1, result.size());
    }

    @Test
    void update_shouldUpdateAndSaveAccount() {
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account update = new Account();
        update.setEmail("new@example.com");

        Account result = accountService.update(update, testAccount.getId());
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void getTotalActiveAccounts_shouldReturnIntValue() {
        when(accountRepository.countActiveAccounts()).thenReturn(100L);
        int total = accountService.getTotalActiveAccounts();
        assertEquals(100, total);
    }

    @Test
    void getTotalActiveAccounts_shouldThrowIfTooBig() {
        when(accountRepository.countActiveAccounts()).thenReturn((long) Integer.MAX_VALUE + 1);
        assertThrows(IllegalStateException.class, () -> accountService.getTotalActiveAccounts());
    }

    @Test
    void search_shouldReturnPageOfAccounts() {
        AccountSearchDto filter = new AccountSearchDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> page = new PageImpl<>(List.of(testAccount));

        when(accountRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Account> result = accountService.search(filter, pageable);

        assertEquals(1, result.getTotalElements());
        verify(accountRepository).findAll(any(Specification.class), eq(pageable));
    }


    @Test
    void findAll_shouldReturnList() {
        when(accountRepository.findAll()).thenReturn(List.of(testAccount));
        List<Account> all = accountService.findAll();
        assertEquals(1, all.size());
    }
}
