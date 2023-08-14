package com.dws.challenge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.sql.converter.custom.configuration.CustomAnnotationsConfig;
import com.dws.challenge.sql.converter.custom.configuration.DataContainerProperties;
import com.dws.challenge.sql.converter.custom.configuration.ExceptionHandlerComponent;
import com.dws.challenge.sql.converter.custom.service.ConverterService;
import com.dws.challenge.sql.converter.custom.service.impl.CombinedConverterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dws.challenge.sql.converter.dto.*;
import jakarta.validation.ValidationException;

import com.dws.challenge.other.service.IsomorphicJsonObjectService;

import lombok.extern.slf4j.Slf4j;

import com.dws.challenge.domain.Account;

import com.dws.challenge.service.CommonDataProperties;

@SpringBootApplication
@Import(CustomAnnotationsConfig.class)
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

	@Autowired
	private DataContainerProperties dataContainerProperties;

	@Autowired
	@Qualifier("queryConverterService")
	private ConverterService queryConverterService;

	@Autowired
	@Qualifier("regexConverterService")
	private ConverterService regexConverterService;

	@Autowired
	private CombinedConverterService combinedConverterService;

	@Autowired
	private ExceptionHandlerComponent exceptionHandlerComponent;

	@Autowired
	private IsomorphicJsonObjectService isomorphicJsonObjectService;

	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		processData();

		String inputQuery = "select BLOB,CONVERT_STR from table custom";
		log.info("input query 1: {}", inputQuery);

		// Test Logic using the Builder Pattern
		QueryConverterServiceData queryConverterServiceData1 = QueryConverterServiceData.builder()
				.sourceDataContainer(DBType.Postgress).targetDataContainer(DBType.Oracle).queryString(inputQuery)
				.filterType(FilterType.DataType).build();
		log.info("Result 1: {}", queryConverterService.convertQuery(queryConverterServiceData1));

		// new regex converterservice
		QueryConverterServiceData queryConverterServiceData21 = QueryConverterServiceData.builder()
				.queryString(inputQuery).sourceDataContainer(DBType.Postgress)
				// .regexType(RegexType.RegexBLOB)
				.build();
		log.info("input query 21: {}", inputQuery);
		Response response21 = null;
		try {
			response21 = regexConverterService.convertQuery(queryConverterServiceData21);
		} catch (ValidationException ex) {
			exceptionHandlerComponent.handleValidationException(ex);
		}
		log.info("Result 21: {}", response21);

		QueryConverterServiceData queryConverterServiceData3 = QueryConverterServiceData.builder()
				.sourceDataContainer(DBType.Postgress).targetDataContainer(DBType.Oracle).queryString(inputQuery)
				.regexType(RegexType.RegexBLOB).filterType(FilterType.DataType).build();
		log.info("input query 3: {}", queryConverterServiceData3);
		Response response3 = combinedConverterService.convertQuery(queryConverterServiceData3);
		log.info("Result 3: {}", response3);

		List<String> includeList = List.of("item1", "item2", "item3", "custom");
		List<String> excludeList = List.of("item4", "item5");

		FileFilterType includeFilter = FileFilterType.INCLUDE_FILTER;
		FileFilterType excludeFilter = FileFilterType.EXCLUDE_FILTER;

		includeFilter.setFilterList(includeList);
		// excludeFilter.setFilterList(excludeList);

		List<FileFilterType> fileFilterList = new ArrayList<>();
		fileFilterList.add(includeFilter);
		// fileFilterList.add(excludeFilter);

		log.info("Include Filter List: " + includeFilter.getFilterList());
		log.info("Exclude Filter List: " + excludeFilter.getFilterList());

		QueryConverterServiceData queryConverterServiceData4 = QueryConverterServiceData.builder()
				.sourceDataContainer(DBType.Postgress).targetDataContainer(DBType.Oracle).queryString(inputQuery)
				.fileFilterList(fileFilterList).fileName("custom").filterType(FilterType.DataType).build();
		log.info("Result 1: {}", queryConverterService.convertQuery(queryConverterServiceData4));

		ObjectMapper objectMapper = new ObjectMapper();

		String json1 = "{\"name\": \"John\", \"address\": [\"address1\", \"address2\"], \"city\": \"New York\"}";
		String json2 = "{\"name\": \"1st\", \"address\": [\"address3\", \"address4\"], \"city\": \"SectionA\"}";

		JsonNode tree1 = null;
		JsonNode tree2 = null;
		try {
			tree1 = objectMapper.readTree(json1);
			tree2 = objectMapper.readTree(json2);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		log.info("Are JSON objects 1 and 2 isomorphic? : {}",
				isomorphicJsonObjectService.compareJsonObjectStructure(tree1, tree2));
	}

	public void processData() {
		for (int i = 0; i < dataContainerProperties.getDataContainer().size(); i++) {
			log.info("dataContainerProperties {}: {}", i, dataContainerProperties.getDataContainer().get(i));
		}
	}

	public void testConcurrency() throws InterruptedException {
		// Create a test instance of AccountsService with mocked dependencies
		// (accountsRepository and notificationService)

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
}
