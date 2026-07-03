package com.ticketa.ticket.kafka

import com.ticketa.ticket.application.service.TicketService
import com.ticketa.ticket.domain.event.PaymentCompletedEvent
import com.ticketa.ticket.infrastructure.kafka.PaymentCompletedEventConsumer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.wheneverBlocking
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentCompletedEventConsumerTest {

    private val ticketService: TicketService = mock()
    private val consumer = PaymentCompletedEventConsumer(ticketService)

    @Test
    fun `should delegate to ticket service`() = runBlocking {
        val event = PaymentCompletedEvent(
            eventId = UUID.randomUUID().toString(),
            paymentId = UUID.randomUUID(),
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = "user-1",
            amount = BigDecimal.valueOf(30)
        )

        wheneverBlocking { ticketService.handlePaymentCompleted(any()) } doReturn mock()

        consumer.onPaymentCompleted(event)

        verify(ticketService).handlePaymentCompleted(event)
    }
}
