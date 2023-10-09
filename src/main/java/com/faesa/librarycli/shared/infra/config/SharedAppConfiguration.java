package com.faesa.librarycli.shared.infra.config;

import com.faesa.librarycli.shared.infra.database.DatabaseProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;

@Configuration
public class SharedAppConfiguration {

    @Bean
    public Connection connection() {
        return DatabaseProvider.getConnection();
    }
}
