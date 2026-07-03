package com.ticketa.ticket.infrastructure.persistence

import com.ticketa.ticket.domain.model.Ticket
import com.ticketa.ticket.domain.model.TicketStatus
import com.ticketa.ticket.domain.model.TicketType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("tickets")
data class TicketEntity(
    @Id
    val id: UUID,
    val reservationId: UUID,
    val paymentId: UUID,
    val userId: String,
    val movieId: String? = null,
    val sessionId: UUID,
    val seatId: UUID,
    val ticketType: String,
    val status: String,
    val qrCodeHash: String? = null,
    val issuedAt: Instant,
    val usedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun toDomain(): Ticket = Ticket(
        id = id,
        reservationId = reservationId,
        paymentId = paymentId,
        userId = userId,
        movieId = movieId,
        sessionId = sessionId,
        seatId = seatId,
        ticketType = TicketType.valueOf(ticketType),
        status = TicketStatus.valueOf(status),
        qrCodeHash = qrCodeHash,
        issuedAt = issuedAt,
        usedAt = usedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(ticket: Ticket): TicketEntity = TicketEntity(
            id = ticket.id,
            reservationId = ticket.reservationId,
            paymentId = ticket.paymentId,
            userId = ticket.userId,
            movieId = ticket.movieId,
            sessionId = ticket.sessionId,
            seatId = ticket.seatId,
            ticketType = ticket.ticketType.name,
            status = ticket.status.name,
            qrCodeHash = ticket.qrCodeHash,
            issuedAt = ticket.issuedAt,
            usedAt = ticket.usedAt,
            createdAt = ticket.createdAt,
            updatedAt = ticket.updatedAt
        )
    }
}
