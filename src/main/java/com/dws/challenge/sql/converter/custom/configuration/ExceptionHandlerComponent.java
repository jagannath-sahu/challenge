package com.dws.challenge.sql.converter.custom.configuration;

import org.springframework.stereotype.Component;

import com.dws.challenge.exception.InvalidFileNameException;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExceptionHandlerComponent {

    public void handleInvalidFileNameException(InvalidFileNameException ex) {
        log.info("Invalid filename: {}", ex.getMessage());
    }

    public void handleValidationException(ValidationException ex) {
        log.info("Validation failed: {}", ex.getMessage());
    }
    
    public void handleIllegalArgumentException(IllegalArgumentException ex) {
        log.info("Illegal Argument: {}", ex.getMessage());
    }
}
