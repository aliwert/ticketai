package com.ticketa.ticket.api.request

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class VerifyTicketRequest(
    @field:NotNull
    val ticketId: UUID
)
