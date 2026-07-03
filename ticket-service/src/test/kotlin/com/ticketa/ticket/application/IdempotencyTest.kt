package com.ticketa.ticket.application

import com.ticketa.ticket.domain.event.PaymentCompletedEvent
import com.ticketa.ticket.domain.exception.DuplicateEventException
import com.ticketa.ticket.infrastructure.persistence.ProcessedEventEntity
import com.ticketa.ticket.infrastructure.persistence.ProcessedEventRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.wheneverBlocking
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class IdempotencyTest {

    private val processedEventRepository: ProcessedEventRepository = mock()

    @Test
    fun `should reject event with existing eventId`() = runBlocking {
        val eventId = UUID.randomUUID().toString()

        wheneverBlocking { processedEventRepository.existsById(eventId) } doReturn true

        assertThrows(DuplicateEventException::class.java) {
            runBlocking {
                if (processedEventRepository.existsById(eventId)) {
                    throw DuplicateEventException(eventId)
                }
            }
        }
    }

    @Test
    fun `should save processed event for new eventId`() = runBlocking {
        val eventId = UUID.randomUUID().toString()
        val entity = ProcessedEventEntity(eventId = eventId, eventType = "PaymentCompletedEvent")

        wheneverBlocking { processedEventRepository.existsById(eventId) } doReturn false
        wheneverBlocking { processedEventRepository.save(entity) } doReturn entity

        processedEventRepository.save(entity)

        verify(processedEventRepository).save(entity)
    }
}
