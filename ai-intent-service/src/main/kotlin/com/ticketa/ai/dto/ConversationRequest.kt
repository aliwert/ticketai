package com.ticketa.ai.dto

import jakarta.validation.constraints.NotBlank

data class ConversationRequest(
    @field:NotBlank(message = "Message must not be blank")
    val message: String,

    val userId: String? = null,

    val conversationId: String? = null
)
