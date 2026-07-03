package com.ticketa.ticket.domain

import com.ticketa.ticket.domain.model.Ticket
import com.ticketa.ticket.domain.model.TicketStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class TicketStateMachineTest {

    private fun createTicket(status: TicketStatus = TicketStatus.ISSUED): Ticket = Ticket(
        id = UUID.randomUUID(),
        reservationId = UUID.randomUUID(),
        paymentId = UUID.randomUUID(),
        userId = "user-1",
        sessionId = UUID.randomUUID(),
        seatId = UUID.randomUUID(),
        status = status,
        issuedAt = Instant.now(),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    @Test
    fun `should start in ISSUED state`() {
        val ticket = createTicket()
        assertEquals(TicketStatus.ISSUED, ticket.status)
    }

    @Test
    fun `should transition from ISSUED to USED`() {
        val ticket = createTicket()
        val used = ticket.markAsUsed()
        assertEquals(TicketStatus.USED, used.status)
        assertNotNull(used.usedAt)
    }

    @Test
    fun `should transition from ISSUED to CANCELLED`() {
        val ticket = createTicket()
        val cancelled = ticket.cancel()
        assertEquals(TicketStatus.CANCELLED, cancelled.status)
    }

    @Test
    fun `should transition from ISSUED to EXPIRED`() {
        val ticket = createTicket()
        val expired = ticket.expire()
        assertEquals(TicketStatus.EXPIRED, expired.status)
    }

    @Test
    fun `should reject markAsUsed from USED state`() {
        val ticket = createTicket(TicketStatus.USED)
        assertThrows(IllegalArgumentException::class.java) { ticket.markAsUsed() }
    }

    @Test
    fun `should reject cancel from USED state`() {
        val ticket = createTicket(TicketStatus.USED)
        assertThrows(IllegalArgumentException::class.java) { ticket.cancel() }
    }

    @Test
    fun `should reject expire from USED state`() {
        val ticket = createTicket(TicketStatus.USED)
        assertThrows(IllegalArgumentException::class.java) { ticket.expire() }
    }

    @Test
    fun `ticket should be immutable except for status transitions`() {
        val ticket = createTicket()
        val used = ticket.markAsUsed()
        assertEquals(TicketStatus.USED, used.status)
        assertEquals(ticket.id, used.id)
        assertEquals(ticket.reservationId, used.reservationId)
        assertEquals(ticket.paymentId, used.paymentId)
    }
}
