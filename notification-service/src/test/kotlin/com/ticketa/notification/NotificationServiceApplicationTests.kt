package com.ticketa.notification

import com.ticketa.notification.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestContainersConfig::class)
class NotificationServiceApplicationTests {

    @Test
    fun contextLoads() {
    }
}
