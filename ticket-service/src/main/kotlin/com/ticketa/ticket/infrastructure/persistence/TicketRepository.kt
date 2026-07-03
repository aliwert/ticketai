package com.ticketa.ticket.infrastructure.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TicketRepository : CoroutineCrudRepository<TicketEntity, UUID> {
    suspend fun findByUserId(userId: String): List<TicketEntity>
    suspend fun findByReservationId(reservationId: UUID): List<TicketEntity>
    suspend fun findByPaymentId(paymentId: UUID): TicketEntity?
}
