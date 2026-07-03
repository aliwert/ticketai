package com.ticketa.ticket.domain.model

import java.time.Instant
import java.util.UUID

data class Ticket(
    val id: UUID,
    val reservationId: UUID,
    val paymentId: UUID,
    val userId: String,
    val movieId: String? = null,
    val sessionId: UUID,
    val seatId: UUID,
    val ticketType: TicketType = TicketType.REGULAR,
    val status: TicketStatus,
    val qrCodeHash: String? = null,
    val issuedAt: Instant,
    val usedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun markAsUsed(): Ticket {
        require(status == TicketStatus.ISSUED) { "Can only use an ISSUED ticket, current: $status" }
        return copy(
            status = TicketStatus.USED,
            usedAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    fun cancel(): Ticket {
        require(status == TicketStatus.ISSUED) { "Can only cancel an ISSUED ticket, current: $status" }
        return copy(
            status = TicketStatus.CANCELLED,
            updatedAt = Instant.now()
        )
    }

    fun expire(): Ticket {
        require(status == TicketStatus.ISSUED) { "Can only expire an ISSUED ticket, current: $status" }
        return copy(
            status = TicketStatus.EXPIRED,
            updatedAt = Instant.now()
        )
    }
}
