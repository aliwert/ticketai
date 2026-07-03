package com.ticketa.ticket.application

import com.ticketa.ticket.application.service.QrCodeGenerator
import com.ticketa.ticket.application.service.TicketService
import com.ticketa.ticket.domain.event.PaymentCompletedEvent
import com.ticketa.ticket.domain.exception.DuplicateEventException
import com.ticketa.ticket.domain.exception.TicketNotFoundException
import com.ticketa.ticket.domain.model.TicketStatus
import com.ticketa.ticket.infrastructure.kafka.TicketEventProducer
import com.ticketa.ticket.infrastructure.persistence.ProcessedEventRepository
import com.ticketa.ticket.infrastructure.persistence.TicketEntity
import com.ticketa.ticket.infrastructure.persistence.TicketRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class TicketServiceTest {

    private val ticketRepository: TicketRepository = mock()
    private val processedEventRepository: ProcessedEventRepository = mock()
    private val qrCodeGenerator: QrCodeGenerator = mock()
    private val eventProducer: TicketEventProducer = mock()
    private val ticketService = TicketService(
        ticketRepository = ticketRepository,
        processedEventRepository = processedEventRepository,
        qrCodeGenerator = qrCodeGenerator,
        eventProducer = eventProducer
    )

    @Test
    fun `should get ticket by id`() = runBlocking {
        val ticketId = UUID.randomUUID()
        val entity = TicketEntity(
            id = ticketId,
            reservationId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            userId = "user-1",
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            ticketType = "REGULAR",
            status = "ISSUED",
            issuedAt = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { ticketRepository.findById(ticketId) } doReturn entity

        val ticket = ticketService.getTicket(ticketId)
        assertEquals(ticketId, ticket.id)
        assertEquals(TicketStatus.ISSUED, ticket.status)
    }

    @Test
    fun `should throw when ticket not found`() {
        val ticketId = UUID.randomUUID()
        wheneverBlocking { ticketRepository.findById(ticketId) } doReturn null

        assertThrows(TicketNotFoundException::class.java) {
            runBlocking { ticketService.getTicket(ticketId) }
        }
    }

    @Test
    fun `should get user tickets`() = runBlocking {
        val userId = "user-1"
        val entity = TicketEntity(
            id = UUID.randomUUID(),
            reservationId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            userId = userId,
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            ticketType = "REGULAR",
            status = "ISSUED",
            issuedAt = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { ticketRepository.findByUserId(userId) } doReturn listOf(entity)

        val tickets = ticketService.getUserTickets(userId)
        assertEquals(1, tickets.size)
        assertEquals(userId, tickets[0].userId)
    }

    @Test
    fun `should handle payment completed and issue ticket`() = runBlocking {
        val event = PaymentCompletedEvent(
            eventId = UUID.randomUUID().toString(),
            paymentId = UUID.randomUUID(),
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = "user-1",
            amount = BigDecimal.valueOf(30)
        )

        wheneverBlocking { processedEventRepository.existsById(event.eventId) } doReturn false
        wheneverBlocking { processedEventRepository.save(any()) } doReturn mock()
        wheneverBlocking { ticketRepository.findById(any()) } doReturn null
        wheneverBlocking { ticketRepository.save(any<TicketEntity>()) } doReturn mock()
        wheneverBlocking { qrCodeGenerator.generate(any()) } doReturn byteArrayOf()
        wheneverBlocking { eventProducer.publishTicketIssued(any()) } doReturn Unit

        val ticket = ticketService.handlePaymentCompleted(event)
        assertEquals(event.userId, ticket.userId)
        assertEquals(TicketStatus.ISSUED, ticket.status)
    }

    @Test
    fun `should reject duplicate payment events`() {
        val event = PaymentCompletedEvent(
            eventId = UUID.randomUUID().toString(),
            paymentId = UUID.randomUUID(),
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = "user-1",
            amount = BigDecimal.valueOf(30)
        )

        wheneverBlocking { processedEventRepository.existsById(event.eventId) } doReturn true

        assertThrows(DuplicateEventException::class.java) {
            runBlocking { ticketService.handlePaymentCompleted(event) }
        }
    }

    @Test
    fun `should verify valid ticket`() = runBlocking {
        val ticketId = UUID.randomUUID()
        val entity = TicketEntity(
            id = ticketId,
            reservationId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            userId = "user-1",
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            ticketType = "REGULAR",
            status = "ISSUED",
            issuedAt = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { ticketRepository.findById(ticketId) } doReturn entity
        wheneverBlocking { ticketRepository.save(any<TicketEntity>()) } doReturn mock()

        val result = ticketService.verifyTicket(ticketId)
        assertEquals(true, result.valid)
    }

    @Test
    fun `should reject already used ticket`() = runBlocking {
        val ticketId = UUID.randomUUID()
        val entity = TicketEntity(
            id = ticketId,
            reservationId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            userId = "user-1",
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            ticketType = "REGULAR",
            status = "USED",
            issuedAt = Instant.now(),
            usedAt = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { ticketRepository.findById(ticketId) } doReturn entity

        val result = ticketService.verifyTicket(ticketId)
        assertEquals(false, result.valid)
        assertEquals("Ticket already used", result.reason)
    }

    @Test
    fun `should reject non-existent ticket`() = runBlocking {
        val ticketId = UUID.randomUUID()
        wheneverBlocking { ticketRepository.findById(ticketId) } doReturn null

        val result = ticketService.verifyTicket(ticketId)
        assertEquals(false, result.valid)
        assertEquals("Ticket not found", result.reason)
    }
}
