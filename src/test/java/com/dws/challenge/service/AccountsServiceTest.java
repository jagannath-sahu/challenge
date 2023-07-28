package com.dws.challenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.domain.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountsServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AccountsService accountsService;

    @BeforeEach
    public void setUp() {
        accountsRepository.clearAccounts();
    }

    @Test
    public void transfer_withSufficientBalance() {
        Account accountFrom = new Account("123");
        accountFrom.setBalance(new BigDecimal("1000"));

        Account accountTo = new Account("456");
        accountTo.setBalance(new BigDecimal("500"));

        accountsRepository.createAccount(accountFrom);
        accountsRepository.createAccount(accountTo);

        // Stub the getAccount method to return the account when called with the correct ID
        doAnswer(invocation -> {
            String accountId = invocation.getArgument(0);
            if (accountId.equals(accountFrom.getAccountId())) {
                return accountFrom;
            } else if (accountId.equals(accountTo.getAccountId())) {
                return accountTo;
            }
            return null;
        }).when(accountsRepository).getAccount(anyString());

        accountsService.transfer("123", "456", new BigDecimal("200"));

        assertEquals(new BigDecimal("800"), accountFrom.getBalance());
        assertEquals(new BigDecimal("700"), accountTo.getBalance());

        verify(notificationService, times(1)).notifyAboutTransfer(eq(accountFrom),
                eq("Amount 200 transferred to Account: 456"));
        verify(notificationService, times(1)).notifyAboutTransfer(eq(accountTo),
                eq("Amount 200 received from Account: 123"));
    }

    @Test
    public void transfer_withInsufficientBalance() {
        Account accountFrom = new Account("123");
        accountFrom.setBalance(new BigDecimal("100"));

        Account accountTo = new Account("456");
        accountTo.setBalance(new BigDecimal("500"));

        accountsRepository.createAccount(accountFrom);
        accountsRepository.createAccount(accountTo);

        // Stub the getAccount method to return the account when called with the correct ID
        doAnswer(invocation -> {
            String accountId = invocation.getArgument(0);
            if (accountId.equals(accountFrom.getAccountId())) {
                return accountFrom;
            } else if (accountId.equals(accountTo.getAccountId())) {
                return accountTo;
            }
            return null;
        }).when(accountsRepository).getAccount(anyString());

        assertThrows(IllegalArgumentException.class, () -> {
            accountsService.transfer("123", "456", new BigDecimal("200")); // The source account has a balance of 100
        });

        assertEquals(new BigDecimal("100"), accountFrom.getBalance());
        assertEquals(new BigDecimal("500"), accountTo.getBalance());

        verify(notificationService, never()).notifyAboutTransfer(any(), any());
    }
}
