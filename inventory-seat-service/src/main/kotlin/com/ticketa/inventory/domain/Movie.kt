package com.ticketa.inventory.domain

import java.time.Instant
import java.util.UUID

data class Movie(
    val id: UUID,
    val title: String,
    val description: String?,
    val genre: String?,
    val durationMinutes: Int,
    val rating: String?,
    val posterUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
