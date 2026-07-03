package com.ticketa.ticket.infrastructure.kafka

import com.ticketa.ticket.domain.event.TicketIssuedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class TicketEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(TicketEventProducer::class.java)

    suspend fun publishTicketIssued(event: TicketIssuedEvent) {
        log.info("Publishing TicketIssuedEvent: ticketId={}", event.ticketId)
        kafkaTemplate.send("ticket.issued", event.eventId, event)
    }
}
