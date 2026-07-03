package com.ticketa.ai.service

import com.ticketa.ai.domain.BookingIntent
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PromptBuilderTest {

    private val promptBuilder = PromptBuilder()

    @Test
    fun `system prompt contains all intents`() {
        val prompt = promptBuilder.buildSystemPrompt()
        for (intent in BookingIntent.entries) {
            assertTrue(prompt.contains(intent.name), "Prompt should contain $intent")
        }
    }

    @Test
    fun `system prompt contains entity extraction instructions`() {
        val prompt = promptBuilder.buildSystemPrompt()
        assertTrue(prompt.contains("entities"))
        assertTrue(prompt.contains("movie"))
        assertTrue(prompt.contains("date"))
        assertTrue(prompt.contains("quantity"))
    }

    @Test
    fun `system prompt requires JSON response format`() {
        val prompt = promptBuilder.buildSystemPrompt()
        assertTrue(prompt.contains("JSON"))
    }

    @Test
    fun `user prompt returns message as-is`() {
        val message = "Book 2 tickets for Deadpool"
        assertEquals(message, promptBuilder.buildUserPrompt(message))
    }
}

private fun assertEquals(expected: String, actual: String) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual)
}
