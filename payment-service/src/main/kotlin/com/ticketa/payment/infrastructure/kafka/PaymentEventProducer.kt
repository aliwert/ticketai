package com.ticketa.payment.infrastructure.kafka

import com.ticketa.payment.domain.event.PaymentCompletedEvent
import com.ticketa.payment.domain.event.PaymentFailedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(PaymentEventProducer::class.java)

    suspend fun publishPaymentCompleted(event: PaymentCompletedEvent) {
        log.info("Publishing PaymentCompletedEvent: paymentId={}", event.paymentId)
        kafkaTemplate.send("payment.completed", event.eventId, event)
    }

    suspend fun publishPaymentFailed(event: PaymentFailedEvent) {
        log.info("Publishing PaymentFailedEvent: paymentId={}", event.paymentId)
        kafkaTemplate.send("payment.failed", event.eventId, event)
    }
}
