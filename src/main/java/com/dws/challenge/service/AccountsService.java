package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	@Getter
	private final NotificationService notificationService;
	
	private final Map<String, Lock> accountsLocks = new ConcurrentHashMap<>();

	@Autowired
	public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
		this.accountsRepository = accountsRepository;
		this.notificationService = notificationService;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public void transfer(String accountFromId, String accountToId, BigDecimal amount) {
        Account accountFrom = accountsRepository.getAccount(accountFromId);
        Account accountTo = accountsRepository.getAccount(accountToId);

        if (accountFrom == null || accountTo == null) {
            throw new IllegalArgumentException("One or both account(s) do not exist.");
        }

        if (accountFrom.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in the account to transfer.");
        }

        // Acquire locks in a consistent order to prevent deadlocks
        Lock lock1 = accountsLocks.computeIfAbsent(accountFromId, k -> new ReentrantLock());
        Lock lock2 = accountsLocks.computeIfAbsent(accountToId, k -> new ReentrantLock());

        if (accountFromId.compareTo(accountToId) < 0) {
            lock1.lock();
            lock2.lock();
        } else {
            lock2.lock();
            lock1.lock();
        }

        try {
            accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
            accountTo.setBalance(accountTo.getBalance().add(amount));

            accountsRepository.updateAccount(accountFrom);
            accountsRepository.updateAccount(accountTo);

            notificationService.notifyAboutTransfer(accountFrom,
                    "Amount " + amount + " transferred to Account: " + accountTo.getAccountId());
            notificationService.notifyAboutTransfer(accountTo,
                    "Amount " + amount + " received from Account: " + accountFrom.getAccountId());
        } finally {
            lock2.unlock();
            lock1.unlock();
        }
    }
}
