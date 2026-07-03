package com.ticketa.ai.event

import com.ticketa.ai.domain.BookingIntent
import java.time.Instant
import java.util.UUID

data class IntentCreatedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val correlationId: String? = null,
    val intent: BookingIntent,
    val confidence: Double,
    val entities: Map<String, String>,
    val rawMessage: String,
    val userId: String? = null,
    val conversationId: String? = null,
    val timestamp: Instant = Instant.now()
)
