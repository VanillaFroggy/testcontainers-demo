package com.example;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class CustomRedisContainer extends GenericContainer<CustomRedisContainer> {

    public CustomRedisContainer() {
        super("redis:7-alpine");
        withExposedPorts(6379);
        waitingFor(Wait.forLogMessage(".*Ready to accept connections.*", 1));
    }
}
