package com.ticketa.ai.service

import com.ticketa.ai.domain.BookingIntent
import com.ticketa.ai.dto.AiIntentResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IntentMapperTest {

    private val mapper = IntentMapper()

    @Test
    fun `maps purchase tickets response to domain`() {
        val aiResponse = AiIntentResponse(
            intent = "PURCHASE_TICKETS",
            confidence = 0.95,
            entities = mapOf("movie" to "Deadpool", "quantity" to "2")
        )
        val result = mapper.toIntentData(aiResponse, "Book 2 tickets for Deadpool")

        assertEquals(BookingIntent.PURCHASE_TICKETS, result.intent)
        assertEquals(0.95, result.confidence)
        assertEquals("Deadpool", result.entities["movie"])
        assertEquals("2", result.entities["quantity"])
        assertEquals("Book 2 tickets for Deadpool", result.rawMessage)
    }

    @Test
    fun `maps unknown intent string to UNKNOWN`() {
        val aiResponse = AiIntentResponse(
            intent = "SOME_GARBAGE",
            confidence = 0.0
        )
        val result = mapper.toIntentData(aiResponse, "random text")

        assertEquals(BookingIntent.UNKNOWN, result.intent)
    }

    @Test
    fun `maps empty response to UNKNOWN`() {
        val result = mapper.toIntentData(AiIntentResponse(), "test")

        assertEquals(BookingIntent.UNKNOWN, result.intent)
        assertEquals(0.0, result.confidence)
    }

    @Test
    fun `case insensitive intent mapping`() {
        val lowerCase = mapper.toIntentData(AiIntentResponse(intent = "purchase_tickets"), "test")
        assertEquals(BookingIntent.PURCHASE_TICKETS, lowerCase.intent)

        val mixedCase = mapper.toIntentData(AiIntentResponse(intent = "Check_Availability"), "test")
        assertEquals(BookingIntent.CHECK_AVAILABILITY, mixedCase.intent)
    }
}
