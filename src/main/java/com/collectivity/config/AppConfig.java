package com.collectivity.config;

import com.collectivity.datasource.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public JdbcDataSource jdbcDataSource() {
        return new JdbcDataSource();
    }
}