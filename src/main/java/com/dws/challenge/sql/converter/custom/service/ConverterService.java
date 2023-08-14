package com.dws.challenge.sql.converter.custom.service;

import com.dws.challenge.sql.converter.dto.QueryConverterServiceData;
import com.dws.challenge.sql.converter.dto.Response;

public interface ConverterService {

	public abstract Response convertQuery(QueryConverterServiceData queryConverterServiceData);
}
