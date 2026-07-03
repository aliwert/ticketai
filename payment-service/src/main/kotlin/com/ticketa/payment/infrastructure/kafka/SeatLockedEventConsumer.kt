package com.ticketa.payment.infrastructure.kafka

import com.ticketa.payment.application.service.PaymentService
import com.ticketa.payment.domain.event.SeatLockedEvent
import com.ticketa.payment.domain.exception.DuplicateEventException
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class SeatLockedEventConsumer(
    private val paymentService: PaymentService
) {
    private val log = LoggerFactory.getLogger(SeatLockedEventConsumer::class.java)

    @KafkaListener(topics = ["seat.locked"], groupId = "payment-service")
    suspend fun onSeatLockedEvent(event: SeatLockedEvent) {
        log.info("Received SeatLockedEvent: reservationId={}", event.reservationId)
        try {
            paymentService.handleSeatLockedEvent(event)
        } catch (e: DuplicateEventException) {
            log.warn("Duplicate event received, skipping: eventId={}", event.eventId)
        } catch (e: Exception) {
            log.error("Error processing SeatLockedEvent: eventId={}", event.eventId, e)
            throw e
        }
    }
}
