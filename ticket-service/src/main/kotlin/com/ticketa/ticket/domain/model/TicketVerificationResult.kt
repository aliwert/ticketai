package com.ticketa.ticket.domain.model

import java.util.UUID

data class TicketVerificationResult(
    val valid: Boolean,
    val ticketId: UUID? = null,
    val reason: String? = null
)
