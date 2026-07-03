package com.ticketa.ai.controller

import com.ticketa.ai.domain.BookingIntent
import com.ticketa.ai.domain.IntentData
import com.ticketa.ai.service.IntentExtractionService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest(properties = ["eureka.client.enabled=false"])
class ConversationControllerTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @MockitoBean
    private lateinit var intentExtractionService: IntentExtractionService

    @Test
    fun `post conversation returns extracted intent`() {
        val intentData = IntentData(
            intent = BookingIntent.PURCHASE_TICKETS,
            confidence = 0.95,
            entities = mapOf("movie" to "Deadpool", "quantity" to "2"),
            rawMessage = "I want to buy 2 tickets for Deadpool"
        )

        Mockito.`when`(intentExtractionService.extractIntent(ArgumentMatchers.anyString()))
            .thenReturn(Mono.just(intentData))

        WebTestClient.bindToApplicationContext(context).build()
            .post().uri("/api/intent/conversation")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"message":"I want to buy 2 tickets for Deadpool"}""")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.intent").isEqualTo("PURCHASE_TICKETS")
            .jsonPath("$.confidence").isEqualTo(0.95)
            .jsonPath("$.entities.movie").isEqualTo("Deadpool")
    }

    @Test
    fun `post conversation with blank message returns 400`() {
        WebTestClient.bindToApplicationContext(context).build()
            .post().uri("/api/intent/conversation")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"message":" "}""")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `post conversation with empty message returns 400`() {
        WebTestClient.bindToApplicationContext(context).build()
            .post().uri("/api/intent/conversation")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"message":""}""")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `post conversation with unknown intent returns response`() {
        val intentData = IntentData(
            intent = BookingIntent.UNKNOWN,
            confidence = 0.2,
            rawMessage = "Some random message"
        )

        Mockito.`when`(intentExtractionService.extractIntent(ArgumentMatchers.anyString()))
            .thenReturn(Mono.just(intentData))

        WebTestClient.bindToApplicationContext(context).build()
            .post().uri("/api/intent/conversation")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"message":"Some random message"}""")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.intent").isEqualTo("UNKNOWN")
            .jsonPath("$.confidence").isEqualTo(0.2)
    }
}
