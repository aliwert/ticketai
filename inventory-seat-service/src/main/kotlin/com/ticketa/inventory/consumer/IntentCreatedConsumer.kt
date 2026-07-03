package com.ticketa.inventory.consumer

import com.ticketa.inventory.event.IntentCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class IntentCreatedConsumer {

    private val log = LoggerFactory.getLogger(IntentCreatedConsumer::class.java)

    @KafkaListener(topics = ["\${app.kafka.intent-topic}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun onIntentCreated(event: IntentCreatedEvent) {
        log.info("Received intent event: id={}, intent={}, userId={}",
            event.eventId, event.intent, event.userId)

        when (event.intent) {
            "PURCHASE_TICKETS", "CHECK_AVAILABILITY" -> {
                log.debug("Intent {} requires seat lookup for user {}", event.intent, event.userId)
            }
            "CANCEL_TICKETS" -> {
                log.debug("Intent CANCEL_TICKETS requires reservation lookup for user {}", event.userId)
            }
            else -> log.debug("Intent {} requires no seat action", event.intent)
        }
    }
}
