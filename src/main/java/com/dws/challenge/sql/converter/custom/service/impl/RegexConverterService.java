package com.dws.challenge.sql.converter.custom.service.impl;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dws.challenge.sql.converter.custom.configuration.DataContainerProperties;
import com.dws.challenge.sql.converter.custom.configuration.RegexConverterServiceMarker;
import com.dws.challenge.sql.converter.dto.*;
import com.dws.challenge.sql.converter.custom.service.ConverterService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegexConverterService implements ConverterService {
	
	//@Autowired
	private DataContainerProperties dataContainerProperties;
	
	//@Autowired
	private final Validator validator;

	@Autowired
	public RegexConverterService(DataContainerProperties dataContainerProperties, Validator validator) {
		super();
		this.dataContainerProperties = dataContainerProperties;
		this.validator = validator;
	}

	@Override
	public Response convertQuery(QueryConverterServiceData queryConverterServiceData) {
		Set<ConstraintViolation<QueryConverterServiceData>> violations = validator.validate(queryConverterServiceData, RegexConverterServiceMarker.class);
        
        if (!violations.isEmpty()) {
        	for (ConstraintViolation<QueryConverterServiceData> violation : violations) {
                System.out.println("Validation error: " + violation.getPropertyPath() + " " + violation.getMessage());
            }
            // Handle validation errors
            throw new ValidationException("Validation failed: " + violations.toString());
        }
        
		RegexData regexData = dataContainerProperties.getRegexDataForTypeAndName(queryConverterServiceData.getSourceDataContainer().name(), queryConverterServiceData.getRegexType());
        if (regexData != null) {
            log.info("Name: {}", regexData.getName());
            log.info("Find: {}", regexData.getFind());
            log.info("Replace: {}", regexData.getReplace());
        } else {
        	log.info("Regex data not found for the given inputs.");
        }
		
		try {
            Pattern pattern = Pattern.compile(regexData.getFind());
            Matcher matcher = pattern.matcher(queryConverterServiceData.getQueryString());
            StringBuffer resultString = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(resultString, regexData.getReplace());
            }
            matcher.appendTail(resultString);

            String originalQueryString = queryConverterServiceData.getQueryString();
            String updatedQueryString = resultString.toString();

            boolean isConverted = !originalQueryString.equals(updatedQueryString);

            return new Response(updatedQueryString, isConverted);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + e.getMessage());
        }
	}

}
