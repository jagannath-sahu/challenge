package com.dws.challenge;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

import com.dws.challenge.domain.Account;

import com.dws.challenge.service.CommonDataProperties;
import com.dws.challenge.domain.DataType;
import com.dws.challenge.domain.FunctionData;

@SpringBootApplication
@Slf4j
public class ChallengeApplication implements CommandLineRunner {
	
	@Autowired
	AccountsRepository accountsRepository;
	
	@Autowired
	NotificationService notificationService;
	
	@Autowired
	AccountsService accountsService;
	
	@Autowired
	CommonDataProperties commonDataProperties;

	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		testConcurrency();
		processData();
	}
	
	public void testConcurrency() throws InterruptedException {
		// Create a test instance of AccountsService with mocked dependencies (accountsRepository and notificationService)

        // Create accounts with initial balances
        Account account1 = new Account("1");
        account1.setBalance(new BigDecimal(500));
        Account account2 = new Account("2");
        account2.setBalance(new BigDecimal(500));
        accountsService.createAccount(account1);
        accountsService.createAccount(account2);

        // Create multiple threads to perform concurrent transfers
        Thread thread1 = new Thread(() -> {
            accountsService.transfer("1", "2", new BigDecimal("50"));
        });

        Thread thread2 = new Thread(() -> {
            accountsService.transfer("2", "1", new BigDecimal("50"));
        });
        
        Thread thread3 = new Thread(() -> {
            accountsService.transfer("1", "2", new BigDecimal("50"));
        });

        Thread thread4 = new Thread(() -> {
            accountsService.transfer("2", "1", new BigDecimal("50"));
        });
        
        Thread thread5 = new Thread(() -> {
            accountsService.transfer("1", "2", new BigDecimal("50"));
        });

        Thread thread6 = new Thread(() -> {
            accountsService.transfer("2", "1", new BigDecimal("50"));
        });
        
        Thread thread7 = new Thread(() -> {
            accountsService.transfer("1", "2", new BigDecimal("50"));
        });
        
        // Start the threads
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();

        // Wait for the threads to complete
        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
        thread5.join();
        thread6.join();
        thread7.join();

        // Print the updated account balances after transfers
        log.info("Account 1 balance: " + accountsService.getAccount("1").getBalance());
        log.info("Account 2 balance: " + accountsService.getAccount("2").getBalance());
	}
	
	public void processData() {
    	String dbName = commonDataProperties.getCommonData().get(0).getName();
    	log.info("DB Name : " + dbName);
    	DataType datatype = commonDataProperties.getCommonData().get(0).getDataType();
    	log.info(dbName + " BinaryLargeObjec: " + datatype.getBinaryLargeObject());
    	log.info(dbName + " CharacterLargeObject: " + datatype.getCharacterLargeObject());
    	FunctionData functionData = commonDataProperties.getCommonData().get(0).getFunctionData();
    	log.info(dbName + " PositionOfSubString: " + functionData.getPositionOfSubString());
    	log.info(dbName + " ConvertToString: " + functionData.getConvertToString());
    	
    	log.info("************************************");
    	
    	dbName = commonDataProperties.getCommonData().get(1).getName();
    	log.info("DB Name : " + dbName);
    	DataType datatype1 = commonDataProperties.getCommonData().get(1).getDataType();
    	log.info(dbName + " BinaryLargeObjec: " + datatype1.getBinaryLargeObject());
    	log.info(dbName + " CharacterLargeObject: " + datatype1.getCharacterLargeObject());
    	FunctionData functionData1 = commonDataProperties.getCommonData().get(1).getFunctionData();
    	log.info(dbName + " PositionOfSubString: " + functionData1.getPositionOfSubString());
    	log.info(dbName + " ConvertToString: " + functionData1.getConvertToString());
    }

}
