package com.ticketa.auth.infrastructure.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RefreshTokenRepository : CoroutineCrudRepository<RefreshTokenEntity, UUID> {
    suspend fun findByToken(token: String): RefreshTokenEntity?
}
