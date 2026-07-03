package com.ticketa.payment.infrastructure.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PaymentRepository : CoroutineCrudRepository<PaymentEntity, UUID> {
    suspend fun findByUserId(userId: String): List<PaymentEntity>
    suspend fun findByReservationId(reservationId: UUID): List<PaymentEntity>
}
