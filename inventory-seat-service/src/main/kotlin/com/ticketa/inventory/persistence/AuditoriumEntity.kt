package com.ticketa.inventory.persistence

import com.ticketa.inventory.domain.Auditorium
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("auditoriums")
data class AuditoriumEntity(
    @Id val id: UUID,
    val name: String,
    @Column("total_rows") val totalRows: Int,
    @Column("total_columns") val totalColumns: Int,
    @Column("created_at") val createdAt: Instant
) {
    fun toDomain(): Auditorium = Auditorium(
        id = id, name = name, totalRows = totalRows,
        totalColumns = totalColumns, createdAt = createdAt
    )
}
