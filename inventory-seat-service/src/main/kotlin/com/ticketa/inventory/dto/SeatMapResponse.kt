package com.ticketa.inventory.dto

data class SeatMapResponse(
    val sessionId: String,
    val auditoriumName: String,
    val totalRows: Int,
    val totalColumns: Int,
    val seats: List<SeatResponse>
)
