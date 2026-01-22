package com.shortLinker.ShortLinker.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DBConfig {

    private static final Logger log = LoggerFactory.getLogger(DBConfig.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle}")
    private int minIdle;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMaximumPoolSize(maxPoolSize);
        ds.setMinimumIdle(minIdle);
        return ds;
    }

    private void testConnection(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            log.info("✅ Database connection established successfully: {}",
                    connection.getMetaData().getURL());
        } catch (Exception ex) {
            log.error("❌ Failed to connect to database", ex);
            throw new RuntimeException("Database connection failed", ex);
        }
    }
}
