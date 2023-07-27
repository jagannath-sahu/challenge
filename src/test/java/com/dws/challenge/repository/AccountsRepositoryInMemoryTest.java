package com.dws.challenge.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.domain.*;

public class AccountsRepositoryInMemoryTest {

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
    public void getAccount_ExistingAccountId_ReturnsAccount() {
        Account account = new Account("123");
        account.setBalance(new BigDecimal("100"));
        accountsRepository.createAccount(account);
        Account result = accountsRepository.getAccount("123");
        assertEquals(account, result);
    }

    @Test
    public void getAccount_NonExistingAccountId_ReturnsNull() {
        Account result = accountsRepository.getAccount("123");
        assertNull(result);
    }

    @Test
    public void updateAccount_ExistingAccountId_SuccessfulUpdate() {
        Account account = new Account("123");
        account.setBalance(new BigDecimal("100"));
        accountsRepository.createAccount(account);
        
        account.setBalance(new BigDecimal("200"));
        accountsRepository.updateAccount(account);

        Account updatedAccount = accountsRepository.getAccount("123");
        assertEquals(new BigDecimal("200"), updatedAccount.getBalance());
    }
}
