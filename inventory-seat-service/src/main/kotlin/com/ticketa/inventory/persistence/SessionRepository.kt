package com.ticketa.inventory.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface SessionRepository : CoroutineCrudRepository<SessionEntity, UUID> {
    suspend fun findByMovieId(movieId: UUID): List<SessionEntity>
    suspend fun findByStartTimeBetween(start: java.time.Instant, end: java.time.Instant): List<SessionEntity>
}
