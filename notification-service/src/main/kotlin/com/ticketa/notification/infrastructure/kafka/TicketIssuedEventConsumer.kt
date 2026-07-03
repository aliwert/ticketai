package com.ticketa.notification.infrastructure.kafka

import com.ticketa.notification.application.service.NotificationService
import com.ticketa.notification.domain.event.TicketIssuedEvent
import com.ticketa.notification.domain.exception.DuplicateEventException
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class TicketIssuedEventConsumer(
    private val notificationService: NotificationService
) {
    private val log = LoggerFactory.getLogger(TicketIssuedEventConsumer::class.java)

    @KafkaListener(topics = ["ticket.issued"], groupId = "notification-service")
    suspend fun onTicketIssuedEvent(event: TicketIssuedEvent) {
        log.info("Received TicketIssuedEvent: ticketId={}", event.ticketId)
        try {
            notificationService.handleTicketIssuedEvent(event)
        } catch (e: DuplicateEventException) {
            log.warn("Duplicate event received, skipping: eventId={}", event.eventId)
        } catch (e: Exception) {
            log.error("Error processing TicketIssuedEvent: eventId={}", event.eventId, e)
            throw e
        }
    }
}
