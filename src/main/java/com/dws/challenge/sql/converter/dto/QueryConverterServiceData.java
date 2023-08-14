package com.dws.challenge.sql.converter.dto;

import java.util.List;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.dws.challenge.sql.converter.custom.configuration.*;

@Data
public class QueryConverterServiceData {
    private String fileName;
    
    @NotNull(message = "sourceDataContainer is required", groups = { QueryConverterServiceMarker.class, RegexConverterServiceMarker.class })
    private DBType sourceDataContainer;
    
    @NotNull(message = "targetDataContainer is required", groups = QueryConverterServiceMarker.class)
    private DBType targetDataContainer; 

    @NotBlank(message = "queryString is required", groups = { QueryConverterServiceMarker.class, RegexConverterServiceMarker.class })
    private String queryString;
    
    @NotNull(message = "filterType is required", groups = QueryConverterServiceMarker.class)
    private FilterType filterType;
    
    @NotNull(message = "regexType is required", groups = RegexConverterServiceMarker.class)
    private RegexType regexType;
    
    private List<FileFilterType> fileFilterList;
    
    public List<String> getFileFilterList(FileFilterType filterType) {
        return filterType.getFilterList();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private QueryConverterServiceData instance = new QueryConverterServiceData();

        public Builder fileName(String fileName) {
            instance.setFileName(fileName);
            return this;
        }

        public Builder sourceDataContainer(DBType sourceDataContainer) {
            instance.setSourceDataContainer(sourceDataContainer);
            return this;
        }

        public Builder targetDataContainer(DBType targetDataContainer) {
            instance.setTargetDataContainer(targetDataContainer);
            return this;
        }

        public Builder queryString(String queryString) {
            instance.setQueryString(queryString);
            return this;
        }

        public Builder filterType(FilterType filterType) {
            instance.setFilterType(filterType);
            return this;
        }
        
        public Builder regexType(RegexType regexType) {
            instance.setRegexType(regexType);
            return this;
        }
        
        public Builder fileFilterList(List<FileFilterType> fileFilterList) {
            instance.setFileFilterList(fileFilterList);
            return this;
        }

        public QueryConverterServiceData build() {
            return instance;
        }
    }
}
