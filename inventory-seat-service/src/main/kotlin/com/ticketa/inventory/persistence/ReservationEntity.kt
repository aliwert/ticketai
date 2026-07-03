package com.ticketa.inventory.persistence

import com.ticketa.inventory.domain.Reservation
import com.ticketa.inventory.domain.ReservationStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("reservations")
data class ReservationEntity(
    @Id val id: UUID,
    @Column("session_id") val sessionId: UUID,
    @Column("seat_id") val seatId: UUID,
    @Column("user_id") val userId: String,
    val status: String,
    @Column("locked_at") val lockedAt: Instant,
    @Column("expires_at") val expiresAt: Instant,
    @Column("confirmed_at") val confirmedAt: Instant?,
    @Column("cancelled_at") val cancelledAt: Instant?,
    @Column("created_at") val createdAt: Instant,
    @Column("updated_at") val updatedAt: Instant
) {
    fun toDomain(): Reservation = Reservation(
        id = id, sessionId = sessionId, seatId = seatId, userId = userId,
        status = ReservationStatus.valueOf(status), lockedAt = lockedAt,
        expiresAt = expiresAt, confirmedAt = confirmedAt,
        cancelledAt = cancelledAt, createdAt = createdAt, updatedAt = updatedAt
    )
}
