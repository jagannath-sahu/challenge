package com.dws.challenge.sql.converter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
    private String resultQueryString;
    
    private boolean isConverted;
}

