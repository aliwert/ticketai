package com.ticketa.ticket.api.response

import com.ticketa.ticket.domain.model.Ticket
import com.ticketa.ticket.domain.model.TicketStatus
import com.ticketa.ticket.domain.model.TicketType
import java.time.Instant
import java.util.UUID

data class TicketResponse(
    val id: UUID,
    val reservationId: UUID,
    val paymentId: UUID,
    val userId: String,
    val movieId: String?,
    val sessionId: UUID,
    val seatId: UUID,
    val ticketType: TicketType,
    val status: TicketStatus,
    val issuedAt: Instant,
    val usedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(ticket: Ticket): TicketResponse = TicketResponse(
            id = ticket.id,
            reservationId = ticket.reservationId,
            paymentId = ticket.paymentId,
            userId = ticket.userId,
            movieId = ticket.movieId,
            sessionId = ticket.sessionId,
            seatId = ticket.seatId,
            ticketType = ticket.ticketType,
            status = ticket.status,
            issuedAt = ticket.issuedAt,
            usedAt = ticket.usedAt,
            createdAt = ticket.createdAt,
            updatedAt = ticket.updatedAt
        )
    }
}

data class TicketVerificationResponse(
    val valid: Boolean,
    val ticketId: UUID?,
    val reason: String?
)
