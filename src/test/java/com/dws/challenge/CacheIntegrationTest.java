package com.dws.challenge;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.dws.challenge.sql.converter.dto.*;
import com.dws.challenge.sql.converter.custom.service.impl.QueryConverterService;

@SpringBootTest
@AutoConfigureCache
public class CacheIntegrationTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private QueryConverterService queryConverterService;

    @Test
    public void testCacheIsUsed() {
        // Given
        String queryString = "select BLOB,CONVERT_STR from table custom";
        QueryConverterServiceData queryData = QueryConverterServiceData.builder()
                .sourceDataContainer(DBType.Postgress)
                .targetDataContainer(DBType.Oracle)
                .queryString(queryString)
                .filterType(FilterType.DataType)
                .build();

        // When
        Response actualResponse1 = queryConverterService.convertQuery(queryData); // First call
        Response actualResponse2 = queryConverterService.convertQuery(queryData); // Second call with the same data

        // Then
        Cache cache = cacheManager.getCache("mxSqlCache");
        Cache.ValueWrapper cachedValue = cache.get(queryData);

        // Assertions
        assertTrue(cachedValue != null && cachedValue.get() != null, "Cache should contain the result.");
        assertEquals(actualResponse1, actualResponse2, "The cached value should be returned on subsequent calls.");
    }
}
