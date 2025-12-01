package com.example.config;

import org.springframework.boot.data.jpa.test.autoconfigure.AutoConfigureDataJpa;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@AutoConfigureDataJpa
@EntityScan("com.example.entity")
@EnableJpaRepositories(basePackages = "com.example.repository")
public class TestConfig {
}
