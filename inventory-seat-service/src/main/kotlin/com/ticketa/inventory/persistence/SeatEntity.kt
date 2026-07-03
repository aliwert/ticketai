package com.ticketa.inventory.persistence

import com.ticketa.inventory.domain.Seat
import com.ticketa.inventory.domain.SeatType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("seats")
data class SeatEntity(
    @Id val id: UUID,
    @Column("auditorium_id") val auditoriumId: UUID,
    @Column("row_number") val rowNumber: Int,
    @Column("column_number") val columnNumber: Int,
    @Column("seat_label") val seatLabel: String,
    @Column("seat_type") val seatType: String,
    @Column("created_at") val createdAt: Instant
) {
    fun toDomain(): Seat = Seat(
        id = id, auditoriumId = auditoriumId, rowNumber = rowNumber,
        columnNumber = columnNumber, seatLabel = seatLabel,
        seatType = SeatType.valueOf(seatType), createdAt = createdAt
    )
}
