package com.dws.challenge.sql.converter.custom.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.dws.challenge.domain.*;
import com.dws.challenge.sql.converter.dto.DataType;
import com.dws.challenge.sql.converter.dto.FunctionData;
import com.dws.challenge.sql.converter.dto.RegexData;
import com.dws.challenge.sql.converter.dto.RegexType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "data-system")
public class DataContainerProperties {

    @Getter
    @Setter
    private List<DataContainer> dataContainer = new ArrayList<>();

    @Data
    public static class DataContainer {
        private DataType dataType;
        private FunctionData functionData;
        private List<RegexData> regexData;
        private String name;
    }

    public RegexData getRegexDataForTypeAndName(String dataContainerType, RegexType regexType) {
        for (DataContainer container : dataContainer) {
            if (container.getName().equalsIgnoreCase(dataContainerType)) {
                List<RegexData> regexDataList = container.getRegexData();
                if (regexDataList != null) {
                    for (RegexData regexData : regexDataList) {
                        if (regexData.getName().equals(regexType.name())) {
                            return regexData;
                        }
                    }
                }
            }
        }
        return null;
    }
}
