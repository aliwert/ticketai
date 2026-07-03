package com.ticketa.ticket

import com.ticketa.ticket.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestContainersConfig::class)
class TicketServiceApplicationTests {

    @Test
    fun contextLoads() {
    }
}
