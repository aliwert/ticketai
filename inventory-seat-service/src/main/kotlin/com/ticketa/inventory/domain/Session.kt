package com.ticketa.inventory.domain

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Session(
    val id: UUID,
    val movieId: UUID,
    val auditoriumId: UUID,
    val startTime: Instant,
    val endTime: Instant,
    val price: BigDecimal,
    val createdAt: Instant
)
