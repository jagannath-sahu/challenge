package com.dws.challenge.util;

import com.dws.challenge.sql.converter.dto.*;

import java.util.List;
import java.util.Optional;

import com.dws.challenge.sql.converter.custom.configuration.DataContainerProperties;

public class QueryConverterUtils {
	
    public static DataType findDataType(List<DataContainerProperties.DataContainer> dataContainers, DBType dataContainerName) {
        return dataContainers.stream()
                .filter(container -> container.getName().equalsIgnoreCase(dataContainerName.name()))
                .map(DataContainerProperties.DataContainer::getDataType)
                .findFirst()
                .orElse(null);
    }

    public static FunctionData findFunctionData(List<DataContainerProperties.DataContainer> dataContainers, DBType dataContainerName) {
        return dataContainers.stream()
                .filter(container -> container.getName().equalsIgnoreCase(dataContainerName.name()))
                .map(DataContainerProperties.DataContainer::getFunctionData)
                .findFirst()
                .orElse(null);
    }
    
    public static boolean checkFileNameInFilters(QueryConverterServiceData queryConverterServiceData) {
        List<String> includeFilterList = Optional.ofNullable(queryConverterServiceData.getFileFilterList(FileFilterType.INCLUDE_FILTER))
                .orElse(List.of());
        List<String> excludeFilterList = Optional.ofNullable(queryConverterServiceData.getFileFilterList(FileFilterType.EXCLUDE_FILTER))
                .orElse(List.of());

        String fileName = queryConverterServiceData.getFileName();
        return (includeFilterList.isEmpty() || includeFilterList.contains(fileName))
                && (excludeFilterList.isEmpty() || !excludeFilterList.contains(fileName));
    }
}
