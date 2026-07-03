package com.ticketa.payment

import com.ticketa.payment.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestContainersConfig::class)
class PaymentServiceApplicationTests {

    @Test
    fun contextLoads() {
    }
}
