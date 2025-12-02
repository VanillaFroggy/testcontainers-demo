package com.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

public class OrgstructureTestDbExtension implements Extension, BeforeAllCallback, BeforeEachCallback {

    protected DataSource dataSource;
    protected JdbcClient jdbc;

    @Override
    public void beforeAll(ExtensionContext context) {
        // Используем H2 в режиме совместимости с PostgreSQL
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");

        dataSource = new HikariDataSource(config);
        jdbc = JdbcClient.create(dataSource);

        runScripts("db/init-schema.sql");
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        runScripts("db/truncate.sql");
    }

    /**
     * Запускает скрипты из указанных SQL-файлов
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

    public JdbcClient getJdbc() {
        return jdbc;
    }
}
