package com.ticketa.inventory.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface ReservationRepository : CoroutineCrudRepository<ReservationEntity, UUID> {
    suspend fun findBySessionIdAndStatus(sessionId: UUID, status: String): List<ReservationEntity>
    suspend fun findByUserIdAndStatus(userId: String, status: String): List<ReservationEntity>
    suspend fun findBySessionId(sessionId: UUID): List<ReservationEntity>
}
