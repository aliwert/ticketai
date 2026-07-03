package com.ticketa.ticket.infrastructure.kafka

import com.ticketa.ticket.application.service.TicketService
import com.ticketa.ticket.domain.event.PaymentCompletedEvent
import com.ticketa.ticket.domain.exception.DuplicateEventException
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentCompletedEventConsumer(
    private val ticketService: TicketService
) {
    private val log = LoggerFactory.getLogger(PaymentCompletedEventConsumer::class.java)

    @KafkaListener(topics = ["payment.completed"], groupId = "ticket-service")
    suspend fun onPaymentCompleted(event: PaymentCompletedEvent) {
        log.info("Received PaymentCompletedEvent: paymentId={}", event.paymentId)
        try {
            ticketService.handlePaymentCompleted(event)
        } catch (e: DuplicateEventException) {
            log.warn("Duplicate event received, skipping: eventId={}", event.eventId)
        } catch (e: Exception) {
            log.error("Error processing PaymentCompletedEvent: eventId={}", event.eventId, e)
            throw e
        }
    }
}
