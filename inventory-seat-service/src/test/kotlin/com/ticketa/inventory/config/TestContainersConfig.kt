package com.ticketa.inventory.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.kafka.KafkaContainer

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfig {

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer("postgres:16-alpine")
    }

    @Bean
    @ServiceConnection("redis")
    fun redisContainer(): GenericContainer<*> {
        return GenericContainer("redis:7-alpine").withExposedPorts(6379)
    }

    @Bean
    @ServiceConnection
    fun kafkaContainer(): KafkaContainer {
        return KafkaContainer("confluentinc/cp-kafka:7.6.1")
    }
}
