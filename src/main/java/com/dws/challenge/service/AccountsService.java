package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;
	
	@Getter
	private final NotificationService notificationService;
	
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

	    CompletableFuture<Void> transferFromTo = CompletableFuture.runAsync(() -> {
	        synchronized (accountFrom) {
	            accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
	            accountsRepository.updateAccount(accountFrom);
	            notificationService.notifyAboutTransfer(accountFrom,
	                    "Amount " + amount + " transferred to Account: " + accountTo.getAccountId());
	        }
	    });

	    CompletableFuture<Void> transferToFrom = CompletableFuture.runAsync(() -> {
	        synchronized (accountTo) {
	            accountTo.setBalance(accountTo.getBalance().add(amount));
	            accountsRepository.updateAccount(accountTo);
	            notificationService.notifyAboutTransfer(accountTo,
	                    "Amount " + amount + " received from Account: " + accountFrom.getAccountId());
	        }
	    });
	   
	    try {
	        CompletableFuture.allOf(transferFromTo, transferToFrom).get();
	    } catch (InterruptedException | ExecutionException e) {
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Transfer failed.");
	    }
	}
}
