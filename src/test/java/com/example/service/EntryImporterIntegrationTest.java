package com.example.service;

import com.example.config.TestConfig;
import com.example.config.TestDbExtension;
import com.example.entity.TestAbstractEntity;
import com.example.repository.TestAbstractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ExtendWith(TestDbExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {TestConfig.class, TestAbstractRepository.class})
class EntryImporterIntegrationTest {

    EntryImporter<TestAbstractEntity> importer;

    @Autowired
    TestAbstractRepository repository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestDbExtension.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", TestDbExtension.POSTGRES::getUsername);
        registry.add("spring.datasource.password", TestDbExtension.POSTGRES::getPassword);
    }

    @BeforeEach
    void setUp() {
        importer = new EntryImporter<>(repository);
    }

    @Test
    void importValid_returnsTrue_whenEntityExists() {
        // Arrange
        var entity = createEntity();

        // Act
        boolean status = importer.importValid(entity);

        // Assert
        assertTrue(status);
    }

    @Test
    void importValid_returnsTrue_whenEntityDoesNotExist() {
        // Arrange
        var entity = createEntity();

        // Act
        boolean firstStatus = importer.importValid(entity);
        boolean secondStatus = importer.importValid(entity);

        // Assert
        assertTrue(firstStatus);
        assertTrue(secondStatus);
    }

    TestAbstractEntity createEntity() {
        var entity = new TestAbstractEntity();
        entity.setExternalId("test_external_id");
        entity.setAuthorEmployeeId(2L);
        entity.setUpdateEmployeeId(3L);
        entity.setName("test_name");
        return entity;
    }
}
