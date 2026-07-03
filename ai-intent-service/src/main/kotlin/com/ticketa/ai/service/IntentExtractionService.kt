package com.ticketa.ai.service

import com.ticketa.ai.domain.BookingIntent
import com.ticketa.ai.domain.IntentData
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IntentExtractionService(
    chatClientBuilder: ChatClient.Builder,
    private val promptBuilder: PromptBuilder,
    private val llmResponseParser: LlmResponseParser,
    private val intentMapper: IntentMapper
) {
    private val log = LoggerFactory.getLogger(IntentExtractionService::class.java)
    private val chatClient: ChatClient = chatClientBuilder.build()

    fun extractIntent(userMessage: String): Mono<IntentData> {
        return Mono.fromCallable {
            val response = chatClient.prompt()
                .system(promptBuilder.buildSystemPrompt())
                .user(promptBuilder.buildUserPrompt(userMessage))
                .call()
                .content()

            val aiResponse = llmResponseParser.parse(response)
            intentMapper.toIntentData(aiResponse, userMessage)
        }.onErrorResume { e ->
            log.error("AI intent extraction failed: {}", e.message)
            Mono.just(
                IntentData(
                    intent = BookingIntent.UNKNOWN,
                    confidence = 0.0,
                    rawMessage = userMessage
                )
            )
        }
    }
}
