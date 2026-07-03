package com.ticketa.ticket.domain.event

import java.time.Instant
import java.util.UUID

data class TicketIssuedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val ticketId: UUID,
    val reservationId: UUID,
    val paymentId: UUID,
    val userId: String,
    val sessionId: UUID,
    val seatId: UUID,
    val timestamp: Instant = Instant.now()
)
