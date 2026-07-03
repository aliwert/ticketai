package com.ticketa.ai.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.ticketa.ai.dto.AiIntentResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LlmResponseParser(private val objectMapper: ObjectMapper) {

    private val log = LoggerFactory.getLogger(LlmResponseParser::class.java)

    fun parse(rawResponse: String?): AiIntentResponse {
        if (rawResponse == null) {
            return AiIntentResponse()
        }
        return try {
            val json = extractJson(rawResponse)
            objectMapper.readValue(json, AiIntentResponse::class.java)
        } catch (e: Exception) {
            log.warn("Failed to parse LLM response: {}", e.message)
            AiIntentResponse()
        }
    }

    internal fun extractJson(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        return if (start >= 0 && end > start) text.substring(start, end + 1) else "{}"
    }
}
