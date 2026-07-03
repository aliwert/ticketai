package com.ticketa.auth.infrastructure.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : CoroutineCrudRepository<UserEntity, UUID> {
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun existsByEmail(email: String): Boolean
}
