package com.ticketa.notification.infrastructure.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepository : CoroutineCrudRepository<NotificationEntity, UUID> {
    suspend fun findByUserId(userId: String): List<NotificationEntity>
    suspend fun findByStatus(status: String): List<NotificationEntity>
}
