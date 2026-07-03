package com.ticketa.payment.application

import com.ticketa.payment.application.service.PaymentService
import com.ticketa.payment.domain.event.SeatLockedEvent
import com.ticketa.payment.domain.exception.DuplicateEventException
import com.ticketa.payment.domain.exception.PaymentNotFoundException
import com.ticketa.payment.domain.model.PaymentStatus
import com.ticketa.payment.infrastructure.kafka.PaymentEventProducer
import com.ticketa.payment.infrastructure.persistence.PaymentEntity
import com.ticketa.payment.infrastructure.persistence.PaymentRepository
import com.ticketa.payment.infrastructure.persistence.ProcessedEventRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentServiceTest {

    private val paymentRepository: PaymentRepository = mock()
    private val processedEventRepository: ProcessedEventRepository = mock()
    private val paymentProcessor = mock<com.ticketa.payment.application.service.MockPaymentProcessor>()
    private val eventProducer: PaymentEventProducer = mock()
    private val paymentService = PaymentService(
        paymentRepository = paymentRepository,
        processedEventRepository = processedEventRepository,
        paymentProcessor = paymentProcessor,
        eventProducer = eventProducer
    )

    @Test
    fun `should get payment by id`() = runBlocking {
        val paymentId = UUID.randomUUID()
        val entity = PaymentEntity(
            id = paymentId,
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = "user-1",
            amount = BigDecimal.valueOf(30),
            status = "PENDING",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { paymentRepository.findById(paymentId) } doReturn entity

        val payment = paymentService.getPayment(paymentId)
        assertEquals(paymentId, payment.id)
        assertEquals(PaymentStatus.PENDING, payment.status)
    }

    @Test
    fun `should throw when payment not found`() {
        val paymentId = UUID.randomUUID()
        wheneverBlocking { paymentRepository.findById(paymentId) } doReturn null

        assertThrows(PaymentNotFoundException::class.java) {
            runBlocking { paymentService.getPayment(paymentId) }
        }
    }

    @Test
    fun `should get payment status`() = runBlocking {
        val paymentId = UUID.randomUUID()
        val entity = PaymentEntity(
            id = paymentId,
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = "user-1",
            amount = BigDecimal.valueOf(30),
            status = "COMPLETED",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { paymentRepository.findById(paymentId) } doReturn entity

        val status = paymentService.getPaymentStatus(paymentId)
        assertEquals(PaymentStatus.COMPLETED, status)
    }

    @Test
    fun `should get user payments`() = runBlocking {
        val userId = "user-1"
        val entity = PaymentEntity(
            id = UUID.randomUUID(),
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = userId,
            amount = BigDecimal.valueOf(30),
            status = "PENDING",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { paymentRepository.findByUserId(userId) } doReturn listOf(entity)

        val payments = paymentService.getUserPayments(userId)
        assertEquals(1, payments.size)
        assertEquals(userId, payments[0].userId)
    }

    @Test
    fun `should ignore duplicate events`() {
        val eventId = UUID.randomUUID().toString()
        val event = SeatLockedEvent(
            eventId = eventId,
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = "user-1",
            quantity = 2,
            expiresAt = Instant.now().plusSeconds(300)
        )

        wheneverBlocking { processedEventRepository.existsById(eventId) } doReturn true

        assertThrows(DuplicateEventException::class.java) {
            runBlocking { paymentService.handleSeatLockedEvent(event) }
        }
    }
}
