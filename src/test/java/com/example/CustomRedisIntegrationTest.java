package com.example;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class CustomRedisIntegrationTest {

    @Container
    static final CustomRedisContainer redis = new CustomRedisContainer();

    @Test
    void testRedisConnectionAndBasicOperations() {
        redis.start();

        // Формируем Redis URI на основе контейнера
        String redisUri = "redis://" + redis.getHost() + ":" + redis.getFirstMappedPort();

        RedisClient redisClient = RedisClient.create(redisUri);
        RedisCommands<String, String> syncCommands = redisClient.connect().sync();

        // Проверяем SET и GET
        syncCommands.set("test-key", "test-value");
        String value = syncCommands.get("test-key");

        assertEquals("test-value", value);

        // Закрываем соединение
        redisClient.shutdown();

        redis.stop();
    }
}
