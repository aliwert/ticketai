package com.ticketa.ai.dto

import com.ticketa.ai.domain.BookingIntent

data class ConversationResponse(
    val intent: BookingIntent,
    val confidence: Double,
    val entities: Map<String, String>,
    val message: String
)
