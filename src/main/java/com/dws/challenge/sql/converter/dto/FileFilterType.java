package com.dws.challenge.sql.converter.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public enum FileFilterType {
    INCLUDE_FILTER,
    EXCLUDE_FILTER;
	
	@Getter
	@Setter
    private List<String> filterList;
}
