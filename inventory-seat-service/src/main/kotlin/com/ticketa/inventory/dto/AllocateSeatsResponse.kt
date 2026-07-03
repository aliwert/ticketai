package com.ticketa.inventory.dto

data class AllocateSeatsResponse(
    val reservationId: String,
    val sessionId: String,
    val seats: List<SeatResponse>,
    val expiresAt: String
)
