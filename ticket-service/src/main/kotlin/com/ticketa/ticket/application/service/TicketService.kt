package com.ticketa.ticket.application.service

import com.ticketa.ticket.domain.event.PaymentCompletedEvent
import com.ticketa.ticket.domain.event.TicketIssuedEvent
import com.ticketa.ticket.domain.exception.DuplicateEventException
import com.ticketa.ticket.domain.exception.InvalidTicketStateException
import com.ticketa.ticket.domain.exception.TicketAlreadyUsedException
import com.ticketa.ticket.domain.exception.TicketNotFoundException
import com.ticketa.ticket.domain.model.Ticket
import com.ticketa.ticket.domain.model.TicketStatus
import com.ticketa.ticket.domain.model.TicketType
import com.ticketa.ticket.domain.model.TicketVerificationResult
import com.ticketa.ticket.infrastructure.kafka.TicketEventProducer
import com.ticketa.ticket.infrastructure.persistence.ProcessedEventEntity
import com.ticketa.ticket.infrastructure.persistence.ProcessedEventRepository
import com.ticketa.ticket.infrastructure.persistence.TicketEntity
import com.ticketa.ticket.infrastructure.persistence.TicketRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val processedEventRepository: ProcessedEventRepository,
    private val qrCodeGenerator: QrCodeGenerator,
    private val eventProducer: TicketEventProducer
) {
    private val log = LoggerFactory.getLogger(TicketService::class.java)

    @Transactional
    suspend fun handlePaymentCompleted(event: PaymentCompletedEvent): Ticket {
        if (processedEventRepository.existsById(event.eventId)) {
            log.debug("Duplicate event ignored: {}", event.eventId)
            throw DuplicateEventException(event.eventId)
        }

        processedEventRepository.save(
            ProcessedEventEntity(eventId = event.eventId, eventType = "PaymentCompletedEvent")
        )

        val ticketId = deterministicTicketId(event.paymentId, event.reservationId, event.seatId)
        val existing = ticketRepository.findById(ticketId)
        if (existing != null) {
            log.info("Ticket already exists for payment: id={}", ticketId)
            return existing.toDomain()
        }

        val ticket = Ticket(
            id = ticketId,
            reservationId = event.reservationId,
            paymentId = event.paymentId,
            userId = event.userId,
            sessionId = event.sessionId,
            seatId = event.seatId,
            ticketType = TicketType.REGULAR,
            status = TicketStatus.ISSUED,
            qrCodeHash = ticketId.toString(),
            issuedAt = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val qrBytes = qrCodeGenerator.generate(ticketId.toString())

        ticketRepository.save(TicketEntity.fromDomain(ticket))

        eventProducer.publishTicketIssued(
            TicketIssuedEvent(
                ticketId = ticket.id,
                reservationId = ticket.reservationId,
                paymentId = ticket.paymentId,
                userId = ticket.userId,
                sessionId = ticket.sessionId,
                seatId = ticket.seatId
            )
        )

        log.info("Ticket issued: id={}, userId={}", ticket.id, ticket.userId)
        return ticket
    }

    suspend fun getTicket(ticketId: UUID): Ticket {
        val entity = ticketRepository.findById(ticketId)
            ?: throw TicketNotFoundException(ticketId)
        return entity.toDomain()
    }

    suspend fun getUserTickets(userId: String): List<Ticket> {
        return ticketRepository.findByUserId(userId).map { it.toDomain() }
    }

    @Transactional
    suspend fun verifyTicket(ticketId: UUID): TicketVerificationResult {
        val entity = ticketRepository.findById(ticketId)
            ?: return TicketVerificationResult(valid = false, reason = "Ticket not found")

        val ticket = entity.toDomain()

        if (ticket.status == TicketStatus.USED) {
            return TicketVerificationResult(
                valid = false,
                ticketId = ticketId,
                reason = "Ticket already used"
            )
        }

        if (ticket.status != TicketStatus.ISSUED) {
            return TicketVerificationResult(
                valid = false,
                ticketId = ticketId,
                reason = "Ticket is in invalid state: ${ticket.status}"
            )
        }

        val used = ticket.markAsUsed()
        ticketRepository.save(TicketEntity.fromDomain(used))

        log.info("Ticket verified and marked as used: id={}", ticketId)
        return TicketVerificationResult(valid = true, ticketId = ticketId)
    }

    private fun deterministicTicketId(paymentId: UUID, reservationId: UUID, seatId: UUID): UUID {
        val combined = "$paymentId-$reservationId-$seatId"
        return UUID.nameUUIDFromBytes(combined.toByteArray())
    }
}
