package com.ticketa.inventory.dto

data class SeatResponse(
    val id: String,
    val rowNumber: Int,
    val columnNumber: Int,
    val seatLabel: String,
    val seatType: String,
    val status: String
)
