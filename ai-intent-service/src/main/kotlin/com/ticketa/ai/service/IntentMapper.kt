package com.ticketa.ai.service

import com.ticketa.ai.domain.BookingIntent
import com.ticketa.ai.domain.IntentData
import com.ticketa.ai.dto.AiIntentResponse
import org.springframework.stereotype.Component

@Component
class IntentMapper {

    fun toIntentData(response: AiIntentResponse, rawMessage: String): IntentData {
        val intent = parseIntent(response.intent)
        return IntentData(
            intent = intent,
            confidence = response.confidence,
            entities = response.entities,
            rawMessage = rawMessage
        )
    }

    private fun parseIntent(raw: String): BookingIntent {
        return try {
            BookingIntent.valueOf(raw.uppercase())
        } catch (e: IllegalArgumentException) {
            BookingIntent.UNKNOWN
        }
    }
}
