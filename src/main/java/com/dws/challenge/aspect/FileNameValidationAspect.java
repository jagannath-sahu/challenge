package com.dws.challenge.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.dws.challenge.sql.converter.dto.QueryConverterServiceData;
import com.dws.challenge.util.QueryConverterUtils;
import com.dws.challenge.exception.*;

@Aspect
@Component
public class FileNameValidationAspect {

    @Before("execution(* com.dws.challenge.sql.converter.custom.service.impl.QueryConverterService.convertQuery(..)) && args(queryConverterServiceData)")
    public void beforeConvertQuery(JoinPoint joinPoint, QueryConverterServiceData queryConverterServiceData) {
        if (queryConverterServiceData.getFileFilterList() != null && queryConverterServiceData.getFileName() != null) {
            boolean flag = QueryConverterUtils.checkFileNameInFilters(queryConverterServiceData);
            if (!flag) {
                throw new InvalidFileNameException("Invalid filename: " + queryConverterServiceData.getFileName());
            }
        }
    }
}