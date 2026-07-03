package com.ticketa.ai.domain

data class IntentData(
    val intent: BookingIntent,
    val confidence: Double,
    val entities: Map<String, String> = emptyMap(),
    val rawMessage: String
)
