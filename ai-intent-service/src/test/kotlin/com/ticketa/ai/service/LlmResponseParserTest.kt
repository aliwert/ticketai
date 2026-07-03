package com.ticketa.ai.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LlmResponseParserTest {

    private val objectMapper = ObjectMapper()
    private val parser = LlmResponseParser(objectMapper)

    @Test
    fun `parse returns known intent when present`() {
        val raw = """{"intent":"PURCHASE_TICKETS","confidence":0.95,"entities":{"movie":"Deadpool"}}"""
        val result = parser.parse(raw)
        assertEquals("PURCHASE_TICKETS", result.intent)
        assertEquals(0.95, result.confidence)
        assertEquals("Deadpool", result.entities["movie"])
    }

    @Test
    fun `parse defaults on null response`() {
        val result = parser.parse(null)
        assertEquals("UNKNOWN", result.intent)
        assertEquals(0.0, result.confidence)
    }

    @Test
    fun `parse defaults on invalid json`() {
        val result = parser.parse("not json at all")
        assertEquals("UNKNOWN", result.intent)
        assertEquals(0.0, result.confidence)
    }

    @Test
    fun `extract json with code block formatting`() {
        val text = "```json\n{\"intent\":\"GET_EVENTS\",\"confidence\":0.8,\"entities\":{}}\n```"
        val result = parser.extractJson(text)
        assertEquals("""{"intent":"GET_EVENTS","confidence":0.8,"entities":{}}""", result)
    }

    @Test
    fun `extract json with surrounding text`() {
        val text = "Here is your JSON: {\"intent\":\"CANCEL_TICKETS\",\"confidence\":0.9,\"entities\":{\"booking_reference\":\"REF123\"}}"
        val result = parser.extractJson(text)
        assertEquals("""{"intent":"CANCEL_TICKETS","confidence":0.9,"entities":{"booking_reference":"REF123"}}""", result)
    }

    @Test
    fun `extract json returns empty on no braces`() {
        val result = parser.extractJson("just plain text with no braces")
        assertEquals("{}", result)
    }
}
