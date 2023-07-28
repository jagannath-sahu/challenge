package com.dws.challenge.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.repository.AccountsRepositoryInMemory;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.web.AccountsController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class AccountsControllerTest {

    @Mock
    private AccountsService accountsService;

    @InjectMocks
    private AccountsController accountsController;
    
    private AccountsRepositoryInMemory accountsRepository;

    @BeforeEach
    public void setUp() {
        accountsRepository = new AccountsRepositoryInMemory();
    }
    
    @Test
    public void createAccount_ValidAccount_SuccessfulCreation() throws DuplicateAccountIdException {
        Account account = new Account("123");
        account.setBalance(new BigDecimal("100"));
        accountsRepository.createAccount(account);

        assertEquals(account, accountsRepository.getAccount("123"));
    }

    @Test
    public void createAccount_ValidAccount_ReturnsHttpStatusCreated() throws DuplicateAccountIdException {
        Account account = new Account("123");
        account.setBalance(new BigDecimal("100"));

        doNothing().when(accountsService).createAccount(any());

        ResponseEntity<Object> responseEntity = accountsController.createAccount(account);

        verify(accountsService).createAccount(account);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void createAccount_DuplicateAccountId_ReturnsHttpStatusBadRequest() throws DuplicateAccountIdException {
        Account account = new Account("123");
        account.setBalance(new BigDecimal("100"));

        doThrow(new DuplicateAccountIdException("Account id 123 already exists!"))
                .when(accountsService).createAccount(account);

        ResponseEntity<Object> responseEntity = accountsController.createAccount(account);

        verify(accountsService).createAccount(account);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Account id 123 already exists!", responseEntity.getBody());
    }

    @Test
    public void getAccount_ExistingAccountId_ReturnsAccount() {
        Account account = new Account("123");
        account.setBalance(new BigDecimal("100"));

        when(accountsService.getAccount("123")).thenReturn(account);

        Account result = accountsController.getAccount("123");

        verify(accountsService).getAccount("123");

        assertNotNull(result);
        assertEquals("123", result.getAccountId());
        assertEquals(new BigDecimal("100"), result.getBalance());
    }

    @Test
    public void getAccount_NonExistingAccountId_ReturnsNull() {
        when(accountsService.getAccount("123")).thenReturn(null);
        Account result = accountsController.getAccount("123");

        verify(accountsService).getAccount("123");

        assertNull(result);
    }

    @Test
    public void transfer_ValidTransfer_ReturnsHttpStatusOk() {
        doNothing().when(accountsService).transfer(anyString(), anyString(), any());
        ResponseEntity<Object> responseEntity = accountsController.transfer("123", "456", new BigDecimal("50"));

        verify(accountsService).transfer("123", "456", new BigDecimal("50"));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void transfer_InvalidTransfer_ReturnsHttpStatusBadRequest() {
        doThrow(new IllegalArgumentException("One or both account(s) do not exist."))
                .when(accountsService).transfer(anyString(), anyString(), any());

        ResponseEntity<Object> responseEntity = accountsController.transfer("123", "456", new BigDecimal("50"));

        verify(accountsService).transfer("123", "456", new BigDecimal("50"));

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("One or both account(s) do not exist.", responseEntity.getBody());
    }
}
