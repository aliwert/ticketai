package com.ticketa.inventory.service.locking

import java.util.UUID

data class LockResult(
    val acquired: Boolean,
    val ownerId: String = ""
)

interface SeatLockService {
    suspend fun acquireLock(sessionId: UUID, seatId: UUID, ownerId: String, ttlMs: Long = 30000): LockResult
    suspend fun releaseLock(sessionId: UUID, seatId: UUID, ownerId: String): Boolean
    suspend fun isLocked(sessionId: UUID, seatId: UUID): Boolean
    suspend fun verifyOwnership(sessionId: UUID, seatId: UUID, ownerId: String): Boolean
}
