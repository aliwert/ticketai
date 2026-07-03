package com.ticketa.inventory.domain

import java.time.Instant
import java.util.UUID

data class Seat(
    val id: UUID,
    val auditoriumId: UUID,
    val rowNumber: Int,
    val columnNumber: Int,
    val seatLabel: String,
    val seatType: SeatType,
    val createdAt: Instant
)

enum class SeatType {
    STANDARD, VIP, ACCESSIBLE
}
