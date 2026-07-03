package com.ticketa.ai.dto

data class AiIntentResponse(
    val intent: String = "UNKNOWN",
    val confidence: Double = 0.0,
    val entities: Map<String, String> = emptyMap()
)
