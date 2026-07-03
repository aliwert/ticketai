package com.ticketa.inventory.persistence

import com.ticketa.inventory.domain.Session
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("sessions")
data class SessionEntity(
    @Id val id: UUID,
    @Column("movie_id") val movieId: UUID,
    @Column("auditorium_id") val auditoriumId: UUID,
    @Column("start_time") val startTime: Instant,
    @Column("end_time") val endTime: Instant,
    val price: BigDecimal,
    @Column("created_at") val createdAt: Instant
) {
    fun toDomain(): Session = Session(
        id = id, movieId = movieId, auditoriumId = auditoriumId,
        startTime = startTime, endTime = endTime, price = price,
        createdAt = createdAt
    )
}
