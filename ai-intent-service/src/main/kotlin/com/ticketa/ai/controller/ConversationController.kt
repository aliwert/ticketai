package com.ticketa.ai.controller

import com.ticketa.ai.dto.ConversationRequest
import com.ticketa.ai.dto.ConversationResponse
import com.ticketa.ai.event.IntentCreatedEvent
import com.ticketa.ai.service.IntentExtractionService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/intent")
class ConversationController(
    private val intentExtractionService: IntentExtractionService,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.kafka.intent-topic}") private val intentTopic: String
) {

    @PostMapping("/conversation", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun processConversation(
        @Valid @RequestBody request: ConversationRequest,
        exchange: ServerWebExchange
    ): Mono<ConversationResponse> {
        val correlationId = exchange.request.headers
            .getFirst("X-Correlation-Id")

        return intentExtractionService.extractIntent(request.message)
            .flatMap { intentData ->
                val event = IntentCreatedEvent(
                    correlationId = correlationId,
                    intent = intentData.intent,
                    confidence = intentData.confidence,
                    entities = intentData.entities,
                    rawMessage = request.message,
                    userId = request.userId,
                    conversationId = request.conversationId
                )

                Mono.fromFuture(kafkaTemplate.send(intentTopic, event.eventId, event))
                    .thenReturn(intentData)
            }
            .map { intentData ->
                val message = buildResponseMessage(intentData.intent.name)
                ConversationResponse(
                    intent = intentData.intent,
                    confidence = intentData.confidence,
                    entities = intentData.entities,
                    message = message
                )
            }
    }

    private fun buildResponseMessage(intent: String): String = when (intent) {
        "PURCHASE_TICKETS" -> "I can help you purchase tickets. Let me check availability."
        "CANCEL_TICKETS" -> "I can help you cancel your booking. Let me find your reservation."
        "CHECK_AVAILABILITY" -> "Let me check the current availability for you."
        "GET_EVENTS" -> "Let me look up the events for you."
        "GET_SCHEDULE" -> "Let me find the schedule for you."
        else -> "I'm not sure I understood. Could you rephrase your request?"
    }
}
