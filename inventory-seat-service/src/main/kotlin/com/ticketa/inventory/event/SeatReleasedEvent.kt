package com.ticketa.inventory.event

import java.time.Instant
import java.util.UUID

data class SeatReleasedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val reservationId: UUID,
    val sessionId: UUID,
    val seatId: UUID,
    val reason: String,
    val timestamp: Instant = Instant.now()
)
