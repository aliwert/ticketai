package com.ticketa.inventory.domain

import java.time.Instant
import java.util.UUID

data class Reservation(
    val id: UUID,
    val sessionId: UUID,
    val seatId: UUID,
    val userId: String,
    val status: ReservationStatus,
    val lockedAt: Instant,
    val expiresAt: Instant,
    val confirmedAt: Instant?,
    val cancelledAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class ReservationStatus {
    LOCKED, CONFIRMED, CANCELLED, EXPIRED
}
