package com.dws.challenge.sql.converter.custom.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.cache.annotation.EnableCaching;

@Configuration
@EnableAspectJAutoProxy
@EnableCaching
public class CustomAnnotationsConfig {
}
