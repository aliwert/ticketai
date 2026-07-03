package com.ticketa.payment.application.service

import com.ticketa.payment.domain.event.PaymentCompletedEvent
import com.ticketa.payment.domain.event.PaymentFailedEvent
import com.ticketa.payment.domain.event.SeatLockedEvent
import com.ticketa.payment.domain.exception.DuplicateEventException
import com.ticketa.payment.domain.exception.InvalidPaymentStateException
import com.ticketa.payment.domain.exception.PaymentNotFoundException
import com.ticketa.payment.domain.exception.PaymentProcessingException
import com.ticketa.payment.domain.model.Payment
import com.ticketa.payment.domain.model.PaymentFailureReason
import com.ticketa.payment.domain.model.PaymentMethod
import com.ticketa.payment.domain.model.PaymentStatus
import com.ticketa.payment.infrastructure.kafka.PaymentEventProducer
import com.ticketa.payment.infrastructure.persistence.PaymentEntity
import com.ticketa.payment.infrastructure.persistence.PaymentRepository
import com.ticketa.payment.infrastructure.persistence.ProcessedEventEntity
import com.ticketa.payment.infrastructure.persistence.ProcessedEventRepository
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val processedEventRepository: ProcessedEventRepository,
    private val paymentProcessor: PaymentProcessor,
    private val eventProducer: PaymentEventProducer
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    @Transactional
    suspend fun handleSeatLockedEvent(event: SeatLockedEvent) {
        if (processedEventRepository.existsById(event.eventId)) {
            log.debug("Duplicate event ignored: {}", event.eventId)
            throw DuplicateEventException(event.eventId)
        }

        processedEventRepository.save(
            ProcessedEventEntity(eventId = event.eventId, eventType = "SeatLockedEvent")
        )

        val payment = Payment(
            id = UUID.randomUUID(),
            reservationId = event.reservationId,
            sessionId = event.sessionId,
            seatId = event.seatId,
            userId = event.userId,
            amount = BigDecimal.valueOf(event.quantity.toLong() * 15),
            status = PaymentStatus.PENDING,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        paymentRepository.save(PaymentEntity.fromDomain(payment))
        log.info("Payment created: id={}, reservationId={}", payment.id, event.reservationId)

        processPayment(payment.id)
    }

    @Transactional
    suspend fun processPayment(paymentId: UUID) {
        val entity = paymentRepository.findById(paymentId)
            ?: throw PaymentNotFoundException(paymentId)

        val payment = entity.toDomain()

        if (payment.status != PaymentStatus.PENDING) {
            throw InvalidPaymentStateException("Payment $paymentId is not in PENDING state")
        }

        val processing = payment.startProcessing()
        paymentRepository.save(PaymentEntity.fromDomain(processing))

        try {
            withTimeout(5000L) {
                val result = paymentProcessor.process(processing)
                handlePaymentResult(processing, result)
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            log.warn("Payment processing timed out: id={}", paymentId)
            handlePaymentFailure(processing, PaymentFailureReason.TIMEOUT)
        } catch (e: PaymentProcessingException) {
            log.error("Payment processing failed: id={}", paymentId, e)
            handlePaymentFailure(processing, PaymentFailureReason.PROCESSING_ERROR)
        }
    }

    private suspend fun handlePaymentResult(payment: Payment, result: PaymentResult) {
        if (result.success) {
            val completed = payment.complete().copy(method = payment.method)
            paymentRepository.save(PaymentEntity.fromDomain(completed))

            eventProducer.publishPaymentCompleted(
                PaymentCompletedEvent(
                    paymentId = completed.id,
                    reservationId = completed.reservationId,
                    sessionId = completed.sessionId,
                    seatId = completed.seatId,
                    userId = completed.userId,
                    amount = completed.amount
                )
            )
            log.info("Payment completed: id={}", completed.id)
        } else {
            val reason = result.failureReason ?: PaymentFailureReason.UNKNOWN
            handlePaymentFailure(payment, reason)
        }
    }

    private suspend fun handlePaymentFailure(payment: Payment, reason: PaymentFailureReason) {
        val failed = payment.fail(reason)
        paymentRepository.save(PaymentEntity.fromDomain(failed))

        eventProducer.publishPaymentFailed(
            PaymentFailedEvent(
                paymentId = failed.id,
                reservationId = failed.reservationId,
                sessionId = failed.sessionId,
                seatId = failed.seatId,
                userId = failed.userId,
                amount = failed.amount,
                reason = reason.name
            )
        )
        log.info("Payment failed: id={}, reason={}", failed.id, reason)
    }

    suspend fun getPayment(paymentId: UUID): Payment {
        val entity = paymentRepository.findById(paymentId)
            ?: throw PaymentNotFoundException(paymentId)
        return entity.toDomain()
    }

    suspend fun getPaymentStatus(paymentId: UUID): PaymentStatus {
        val entity = paymentRepository.findById(paymentId)
            ?: throw PaymentNotFoundException(paymentId)
        return entity.toDomain().status
    }

    suspend fun getUserPayments(userId: String): List<Payment> {
        return paymentRepository.findByUserId(userId).map { it.toDomain() }
    }
}
