package com.ticketa.inventory.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface SeatRepository : CoroutineCrudRepository<SeatEntity, UUID> {
    suspend fun findByAuditoriumId(auditoriumId: UUID): List<SeatEntity>
}
