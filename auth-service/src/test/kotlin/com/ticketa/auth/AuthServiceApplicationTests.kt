package com.ticketa.auth

import com.ticketa.auth.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestContainersConfig::class)
class AuthServiceApplicationTests {

    @Test
    fun contextLoads() {
    }
}
