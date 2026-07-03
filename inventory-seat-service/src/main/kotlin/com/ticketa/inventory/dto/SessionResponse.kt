package com.ticketa.inventory.dto

import java.math.BigDecimal

data class SessionResponse(
    val id: String,
    val movieId: String,
    val movieTitle: String,
    val auditoriumId: String,
    val auditoriumName: String,
    val startTime: String,
    val endTime: String,
    val price: BigDecimal
)
