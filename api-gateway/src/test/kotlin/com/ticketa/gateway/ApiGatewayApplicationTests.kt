package com.ticketa.gateway

import com.ticketa.gateway.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestContainersConfig::class)
class ApiGatewayApplicationTests {

    @Test
    fun contextLoads() {
    }
}
