package com.ticketa.ai

import com.ticketa.ai.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestContainersConfig::class)
class AiIntentServiceApplicationTests {

    @Test
    fun contextLoads() {
    }
}
