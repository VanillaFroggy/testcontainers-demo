package com.example;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class MyIntegrationTest {

    @Container
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17.2")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Test
    void testDatabaseConnection() {
        postgres.start();
        try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        )) {
            assertNotNull(conn, "Соединение с базой данных не должно быть null");
            assertTrue(conn.isValid(5), "Соединение должно быть активным");

            // Проверяем, что можно выполнить простой запрос
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {

                assertTrue(rs.next(), "Запрос 'SELECT 1' должен вернуть хотя бы одну строку");
                assertEquals(1, rs.getInt(1), "Значение должно быть 1");
            }
        } catch (SQLException e) {
            fail("Ошибка при работе с базой данных: " + e.getMessage());
        }
        postgres.stop();
    }
}
