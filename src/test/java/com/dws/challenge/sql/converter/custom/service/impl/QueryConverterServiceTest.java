package com.dws.challenge.sql.converter.custom.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.dws.challenge.sql.converter.custom.configuration.DataContainerProperties;
import com.dws.challenge.sql.converter.dto.*;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.util.Collections;
import static org.mockito.Mockito.*;

class QueryConverterServiceTest {

	@Mock
	private DataContainerProperties dataContainerProperties;

	@Mock
	private Validator validator;

	private QueryConverterService queryConverterService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		queryConverterService = new QueryConverterService(dataContainerProperties, validator);
	}

	@Test
	void testConvertQueryWithValidInput() throws IOException {
		// Mocking
		QueryConverterServiceData queryConverterServiceData = new QueryConverterServiceData();
		queryConverterServiceData.setQueryString("SELECT * FROM table");
		queryConverterServiceData.setSourceDataContainer(DBType.Postgress);
		queryConverterServiceData.setTargetDataContainer(DBType.Oracle);
		queryConverterServiceData.setFilterType(FilterType.DataType);

		when(dataContainerProperties.getDataContainer()).thenReturn(Collections.emptyList());

		// Testing
		Response response = queryConverterService.convertQuery(queryConverterServiceData);

		// Assertions
		assertNotNull(response);
		assertEquals("SELECT * FROM table", response.getResultQueryString());
		assertFalse(response.isConverted());

		// Verify interactions
		verify(validator).validate(any(), any());
		verify(dataContainerProperties).getDataContainer();
	}

	// Add more test cases as needed
}
