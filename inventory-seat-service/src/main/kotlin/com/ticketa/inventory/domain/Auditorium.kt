package com.ticketa.inventory.domain

import java.time.Instant
import java.util.UUID

data class Auditorium(
    val id: UUID,
    val name: String,
    val totalRows: Int,
    val totalColumns: Int,
    val createdAt: Instant
)
