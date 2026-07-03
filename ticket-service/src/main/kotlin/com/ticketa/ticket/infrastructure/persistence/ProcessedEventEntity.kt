package com.ticketa.ticket.infrastructure.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("processed_events")
data class ProcessedEventEntity(
    @Id
    val eventId: String,
    val eventType: String,
    val processedAt: Instant = Instant.now()
)
