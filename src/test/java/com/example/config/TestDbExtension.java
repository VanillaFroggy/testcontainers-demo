package com.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;

@Testcontainers
public class TestDbExtension implements Extension, BeforeEachCallback, BeforeAllCallback, AfterAllCallback {

    @Container
    public static final PostgreSQLContainer POSTGRES =
        new PostgreSQLContainer("postgres:17.2")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("db/init-schema.sql");

    protected DataSource dataSource;

    @Override
    public void beforeAll(ExtensionContext context) {
        POSTGRES.start();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(POSTGRES.getJdbcUrl());
        config.setUsername(POSTGRES.getUsername());
        config.setPassword(POSTGRES.getPassword());

        dataSource = new HikariDataSource(config);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        POSTGRES.stop();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        runScripts("db/truncate.sql");
    }

    /**
     * Запускает скрипты из указанных SQL-файлов в БД контейнера
     *
     * <p>Пути должны быть относительно <code>src/test/resources</code>
     */
    public void runScripts(String... paths) {
        ResourceDatabasePopulator dbPopulator = new ResourceDatabasePopulator();
        for (String path : paths) {
            dbPopulator.addScript(new ClassPathResource(path));
        }
        dbPopulator.execute(dataSource);
    }
}
