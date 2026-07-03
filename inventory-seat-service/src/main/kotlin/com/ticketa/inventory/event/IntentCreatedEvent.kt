package com.ticketa.inventory.event

data class IntentCreatedEvent(
    val eventId: String,
    val correlationId: String?,
    val intent: String,
    val confidence: Double,
    val entities: Map<String, String>,
    val rawMessage: String,
    val userId: String?,
    val conversationId: String?,
    val timestamp: String
)
