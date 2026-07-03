package com.ticketa.notification.kafka

import com.ticketa.notification.application.service.NotificationService
import com.ticketa.notification.domain.event.TicketIssuedEvent
import com.ticketa.notification.infrastructure.kafka.TicketIssuedEventConsumer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.wheneverBlocking
import java.util.UUID

class TicketIssuedEventConsumerTest {

    private val notificationService: NotificationService = mock()
    private val consumer = TicketIssuedEventConsumer(notificationService)

    @Test
    fun `should delegate to notification service`() = runBlocking {
        val event = TicketIssuedEvent(
            eventId = UUID.randomUUID().toString(),
            ticketId = UUID.randomUUID(),
            reservationId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            userId = "user-1",
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID()
        )

        wheneverBlocking { notificationService.handleTicketIssuedEvent(any()) } doReturn Unit

        consumer.onTicketIssuedEvent(event)

        verify(notificationService).handleTicketIssuedEvent(event)
    }
}
