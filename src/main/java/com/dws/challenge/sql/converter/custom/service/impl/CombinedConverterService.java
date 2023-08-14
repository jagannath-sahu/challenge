package com.dws.challenge.sql.converter.custom.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.dws.challenge.sql.converter.dto.QueryConverterServiceData;
import com.dws.challenge.sql.converter.dto.Response;
import com.dws.challenge.exception.InvalidFileNameException;

@Service
public class CombinedConverterService {

    private final QueryConverterService queryConverterService;
    private final RegexConverterService regexConverterService;

    @Autowired
    public CombinedConverterService(QueryConverterService queryConverterService, RegexConverterService regexConverterService) {
        this.queryConverterService = queryConverterService;
        this.regexConverterService = regexConverterService;
    }

    public Response convertQuery(QueryConverterServiceData queryConverterServiceData) throws InvalidFileNameException {
        // First, apply QueryConverterService
        Response intermediateResult = queryConverterService.convertQuery(queryConverterServiceData);
        
        queryConverterServiceData.setQueryString(intermediateResult.getResultQueryString());

        // Then, apply RegexConverterService on the intermediate result
        Response finalResult = regexConverterService.convertQuery(queryConverterServiceData);

        // Set isConverted flag to true if either QueryConverterService or RegexConverterService changed the input
        boolean isConverted = intermediateResult.isConverted() || finalResult.isConverted();

        return new Response(finalResult.getResultQueryString(), isConverted);
    }
}
