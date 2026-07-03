package com.ticketa.ticket.controller

import com.ticketa.ticket.api.request.VerifyTicketRequest
import com.ticketa.ticket.application.service.TicketService
import com.ticketa.ticket.domain.model.Ticket
import com.ticketa.ticket.domain.model.TicketStatus
import com.ticketa.ticket.domain.model.TicketType
import com.ticketa.ticket.domain.model.TicketVerificationResult
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import java.time.Instant
import java.util.UUID

class TicketControllerTest {

    private val ticketService: TicketService = mock()
    private val controller = TicketController(ticketService)

    @Test
    fun `should return ticket by id`() = runBlocking {
        val ticketId = UUID.randomUUID()
        val ticket = Ticket(
            id = ticketId,
            reservationId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            userId = "user-1",
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            ticketType = TicketType.REGULAR,
            status = TicketStatus.ISSUED,
            issuedAt = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { ticketService.getTicket(ticketId) } doReturn ticket

        val response = controller.getTicket(ticketId)
        assertEquals(200, response.statusCode.value())
        assertNotNull(response.body)
        assertEquals(ticketId, response.body!!.id)
    }

    @Test
    fun `should return user tickets`() = runBlocking {
        val userId = "user-1"
        val ticket = Ticket(
            id = UUID.randomUUID(),
            reservationId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            userId = userId,
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            ticketType = TicketType.REGULAR,
            status = TicketStatus.ISSUED,
            issuedAt = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { ticketService.getUserTickets(userId) } doReturn listOf(ticket)

        val response = controller.getUserTickets(userId)
        assertEquals(200, response.statusCode.value())
        assertEquals(1, response.body!!.size)
        assertEquals(userId, response.body!![0].userId)
    }

    @Test
    fun `should verify valid ticket`() = runBlocking {
        val ticketId = UUID.randomUUID()
        val request = VerifyTicketRequest(ticketId = ticketId)
        wheneverBlocking { ticketService.verifyTicket(ticketId) } doReturn TicketVerificationResult(valid = true, ticketId = ticketId)

        val response = controller.verifyTicket(request)
        assertEquals(200, response.statusCode.value())
        assertEquals(true, response.body!!.valid)
    }

    @Test
    fun `should reject invalid ticket verification`() = runBlocking {
        val ticketId = UUID.randomUUID()
        val request = VerifyTicketRequest(ticketId = ticketId)
        wheneverBlocking { ticketService.verifyTicket(ticketId) } doReturn TicketVerificationResult(valid = false, reason = "Ticket not found")

        val response = controller.verifyTicket(request)
        assertEquals(422, response.statusCode.value())
        assertEquals(false, response.body!!.valid)
    }
}
