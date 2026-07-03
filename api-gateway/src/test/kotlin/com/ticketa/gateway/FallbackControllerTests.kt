package com.ticketa.gateway

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(properties = ["eureka.client.enabled=false"])
class FallbackControllerTests {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun `fallback auth returns 503`() {
        WebTestClient.bindToApplicationContext(context).build()
            .get().uri("/fallback/auth")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectBody().jsonPath("$.service").isEqualTo("auth")
    }

    @Test
    fun `fallback intent returns 503`() {
        WebTestClient.bindToApplicationContext(context).build()
            .get().uri("/fallback/intent")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
    }

    @Test
    fun `fallback inventory returns 503`() {
        WebTestClient.bindToApplicationContext(context).build()
            .get().uri("/fallback/inventory")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
    }

    @Test
    fun `fallback payment returns 503`() {
        WebTestClient.bindToApplicationContext(context).build()
            .get().uri("/fallback/payment")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
    }

    @Test
    fun `fallback ticket returns 503`() {
        WebTestClient.bindToApplicationContext(context).build()
            .get().uri("/fallback/ticket")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
    }

    @Test
    fun `fallback notification returns 503`() {
        WebTestClient.bindToApplicationContext(context).build()
            .get().uri("/fallback/notification")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
    }
}
