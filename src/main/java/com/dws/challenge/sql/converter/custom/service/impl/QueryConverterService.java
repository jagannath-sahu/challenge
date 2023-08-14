package com.dws.challenge.sql.converter.custom.service.impl;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.dws.challenge.sql.converter.custom.configuration.*;
import com.dws.challenge.sql.converter.dto.*;
import com.dws.challenge.sql.converter.custom.service.ConverterService;
import com.dws.challenge.util.QueryConverterUtils;

import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Service
@Slf4j
public class QueryConverterService implements ConverterService {

	private final DataContainerProperties dataContainerProperties;
	
	private final Validator validator;
	
	@Autowired
	public QueryConverterService(DataContainerProperties dataContainerProperties, Validator validator) {
		this.dataContainerProperties = dataContainerProperties;
		this.validator = validator;
	}

	@Override
	@Cacheable("mxSqlCache")
	public Response convertQuery(QueryConverterServiceData queryConverterServiceData) {
		Set<ConstraintViolation<QueryConverterServiceData>> violations = validator.validate(queryConverterServiceData, QueryConverterServiceMarker.class);
        
        if (!violations.isEmpty()) {
        	for (ConstraintViolation<QueryConverterServiceData> violation : violations) {
                System.out.println("Validation error: " + violation.getPropertyPath() + " " + violation.getMessage());
            }
            // Handle validation errors
            throw new ValidationException("Validation failed: " + violations.toString());
        }

		if (queryConverterServiceData.getFileFilterList() != null) {
			List<FileFilterType> fileFilterList = queryConverterServiceData.getFileFilterList();
			int index = 0;
			for (FileFilterType fileFilter : fileFilterList) {
				log.info("FileFilterList {}: {}", index, fileFilter.name());
				log.info("FileFilterList {}: {}", index, fileFilter.getFilterList());
				index++;
			}
		}

		List<DataContainerProperties.DataContainer> dataContainers = dataContainerProperties.getDataContainer();

		DataType sourceDataType = QueryConverterUtils.findDataType(dataContainers,
				queryConverterServiceData.getSourceDataContainer());
		DataType targetDataType = QueryConverterUtils.findDataType(dataContainers,
				queryConverterServiceData.getTargetDataContainer());

		FunctionData sourceFunctionData = QueryConverterUtils.findFunctionData(dataContainers,
				queryConverterServiceData.getSourceDataContainer());
		FunctionData targetFunctionData = QueryConverterUtils.findFunctionData(dataContainers,
				queryConverterServiceData.getTargetDataContainer());

		String originalQueryString = queryConverterServiceData.getQueryString();
		String updatedQueryString = originalQueryString;

		boolean isConverted = false;

		FilterType filterType = queryConverterServiceData.getFilterType();

		switch (filterType) {
		case DataType:
			if (sourceDataType != null && targetDataType != null) {
				updatedQueryString = replaceWordsInQuery(updatedQueryString, sourceDataType, targetDataType);
				isConverted = !originalQueryString.equals(updatedQueryString);
			}
			break;
		case FunctionData:
			if (sourceFunctionData != null && targetFunctionData != null) {
				updatedQueryString = replaceWordsInQuery(updatedQueryString, sourceFunctionData, targetFunctionData);
				isConverted = !originalQueryString.equals(updatedQueryString);
			}
			break;
		case All:
			if (sourceDataType != null && targetDataType != null) {
				updatedQueryString = replaceWordsInQuery(updatedQueryString, sourceDataType, targetDataType);
				isConverted = !originalQueryString.equals(updatedQueryString);
			}
			if (sourceFunctionData != null && targetFunctionData != null) {
				updatedQueryString = replaceWordsInQuery(updatedQueryString, sourceFunctionData, targetFunctionData);
				isConverted = !originalQueryString.equals(updatedQueryString);
			}
			break;
		default:
			break;
		}
		return new Response(updatedQueryString, isConverted);
	}

	private String replaceWordsInQuery(String queryString, Object sourceData, Object targetData) {
		Class<?> dataClass = sourceData.getClass();
		Field[] fields = dataClass.getDeclaredFields();

		for (Field field : fields) {
			field.setAccessible(true);
			try {
				Object sourceValue = field.get(sourceData);
				Object targetValue = field.get(targetData);

				if (sourceValue != null && targetValue != null) {
					queryString = queryString.replaceAll("\\b" + Pattern.quote(sourceValue.toString()) + "\\b",
							Matcher.quoteReplacement(targetValue.toString()));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return queryString;
	}
}
