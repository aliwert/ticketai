package com.ticketa.payment.domain.event

import java.time.Instant
import java.util.UUID

data class SeatLockedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val reservationId: UUID,
    val sessionId: UUID,
    val seatId: UUID,
    val userId: String,
    val quantity: Int,
    val expiresAt: Instant,
    val timestamp: Instant = Instant.now()
)
