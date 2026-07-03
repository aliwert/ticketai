package com.ticketa.payment.kafka

import com.ticketa.payment.application.service.PaymentService
import com.ticketa.payment.domain.event.SeatLockedEvent
import com.ticketa.payment.infrastructure.kafka.SeatLockedEventConsumer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.wheneverBlocking
import java.time.Instant
import java.util.UUID

class SeatLockedEventConsumerTest {

    private val paymentService: PaymentService = mock()
    private val consumer = SeatLockedEventConsumer(paymentService)

    @Test
    fun `should delegate to payment service`() = runBlocking {
        val event = SeatLockedEvent(
            eventId = UUID.randomUUID().toString(),
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = "user-1",
            quantity = 2,
            expiresAt = Instant.now().plusSeconds(300)
        )

        wheneverBlocking { paymentService.handleSeatLockedEvent(any()) } doReturn Unit

        consumer.onSeatLockedEvent(event)

        verify(paymentService).handleSeatLockedEvent(event)
    }
}
