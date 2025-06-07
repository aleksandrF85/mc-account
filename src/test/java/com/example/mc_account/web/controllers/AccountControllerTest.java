package com.example.mc_account.web.controllers;

import com.example.mc_account.dto.AccountDataDto;
import com.example.mc_account.dto.AccountMeDto;
import com.example.mc_account.dto.AccountResponseDto;
import com.example.mc_account.dto.AccountUpdateDto;
import com.example.mc_account.mapper.AccountMapper;
import com.example.mc_account.model.Account;
import com.example.mc_account.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountServiceImpl;
    @MockBean
    private AccountMapper accountMapper;
    @MockBean
    private FriendsWebClientService friendsWebClientService;
    @MockBean
    private KafkaProducerService eventProducerService;
    @MockBean
    private AccountEventFactoryService eventFactoryService;
    @MockBean
    private NotificationAsyncService notificationAsyncService;

    private final String bearerToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWQiOiJkYzU4Y2JmZC03ZTFjLTRjMmItOTUxOC0xNjM3NWNkMTg3YzMiLCJyb2xlcyI6WyJVU0VSIl0sImlhdCI6MTcxNzY2MDAwMCwiZXhwIjoxNzE3NzAwMDAwfQ.9ZQwo7PMYSoGkfs9-WT8pgSGPXoMN_Ow7PuGKH9_bXQ";

    private Account testAccount;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("dc58cbfd-7e1c-4c2b-9518-16375cd187c3");
        testAccount = new Account();
        testAccount.setId(userId);
        testAccount.setEmail("user@example.com");

        Mockito.when(accountServiceImpl.findByEmail(anyString())).thenReturn(testAccount);
    }

    @Test
    void getCurrentAccount_shouldReturnMeDto() throws Exception {
        Mockito.when(accountServiceImpl.findById(any())).thenReturn(testAccount);
        Mockito.when(friendsWebClientService.getFriendsIds(anyString())).thenReturn(Collections.emptyList());
        Mockito.when(accountMapper.accountToMeDto(any())).thenReturn(new AccountMeDto());

        mockMvc.perform(get("/api/v1/account/me")
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk());
    }

    @Test
    void updateCurrentAccount_shouldReturnUpdatedDto() throws Exception {
        AccountUpdateDto updateDto = new AccountUpdateDto();
        AccountMeDto updatedDto = new AccountMeDto();

        Mockito.when(accountServiceImpl.update(any(), eq(userId))).thenReturn(testAccount);
        Mockito.when(accountMapper.accountToMeDto(any())).thenReturn(updatedDto);
        Mockito.when(accountMapper.updateDtoToAccount(any())).thenReturn(testAccount);

        mockMvc.perform(put("/api/v1/account/me")
                        .header("Authorization", bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCurrentAccount_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/account/me")
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk());
    }

    @Test
    void getAccountByEmail_shouldReturnDto() throws Exception {
        Mockito.when(accountMapper.accountToResponseDto(any())).thenReturn(new AccountResponseDto());

        mockMvc.perform(get("/api/v1/account")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void createAccount_shouldReturnCreatedDto() throws Exception {
        AccountMeDto dto = new AccountMeDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("password123");

        Mockito.when(accountMapper.meDtoToAccount(any())).thenReturn(testAccount);
        Mockito.when(accountServiceImpl.create(any())).thenReturn(testAccount);
        Mockito.when(accountMapper.accountToMeDto(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "password": "password123"
                        }
                    """))
                .andExpect(status().isCreated());
    }

    @Test
    void lastAction_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/v1/account/lastAction/{id}", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void getAccountById_shouldReturnDto() throws Exception {
        Mockito.when(accountServiceImpl.findById(any())).thenReturn(testAccount);
        Mockito.when(accountMapper.accountToDataDto(any())).thenReturn(new AccountDataDto());

        mockMvc.perform(get("/api/v1/account/{id}", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteById_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/account/{id}", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void blockById_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/v1/account/{id}", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void getTotalAccountsCount_shouldReturnInt() throws Exception {
        Mockito.when(accountServiceImpl.getTotalActiveAccounts()).thenReturn(5);

        mockMvc.perform(get("/api/v1/account/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}
