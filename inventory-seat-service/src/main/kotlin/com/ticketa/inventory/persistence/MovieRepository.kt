package com.ticketa.inventory.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface MovieRepository : CoroutineCrudRepository<MovieEntity, UUID> {
    suspend fun findByGenre(genre: String): List<MovieEntity>
}
