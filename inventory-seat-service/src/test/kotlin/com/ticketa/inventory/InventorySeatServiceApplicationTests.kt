package com.ticketa.inventory

import com.ticketa.inventory.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestContainersConfig::class)
class InventorySeatServiceApplicationTests {

    @Test
    fun contextLoads() {
    }
}
