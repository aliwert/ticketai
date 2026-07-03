package com.ticketa.auth.domain.model

import java.time.Instant
import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val roles: Set<Role>,
    val createdAt: Instant,
    val updatedAt: Instant
)
