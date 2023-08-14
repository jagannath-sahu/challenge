package com.dws.challenge.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.dws.challenge.sql.converter.dto.*;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "data")
public class CommonDataProperties {

    private List<CommonData> commonData = new ArrayList<>();

    public List<CommonData> getCommonData() {
        return commonData;
    }

    public void setCommonData(List<CommonData> commonData) {
        this.commonData = commonData;
    }

    @Data
    public static class CommonData {
        private DataType dataType;
        private FunctionData functionData;
        private String name;
    }
}
