package com.ticketa.payment.domain

import com.ticketa.payment.domain.model.Payment
import com.ticketa.payment.domain.model.PaymentFailureReason
import com.ticketa.payment.domain.model.PaymentStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentStateMachineTest {

    private fun createPayment(status: PaymentStatus = PaymentStatus.PENDING): Payment = Payment(
        id = UUID.randomUUID(),
        reservationId = UUID.randomUUID(),
        sessionId = UUID.randomUUID(),
        seatId = UUID.randomUUID(),
        userId = "user-1",
        amount = BigDecimal.valueOf(30),
        status = status,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    @Test
    fun `should start in PENDING state`() {
        val payment = createPayment()
        assertEquals(PaymentStatus.PENDING, payment.status)
    }

    @Test
    fun `should transition from PENDING to PROCESSING`() {
        val payment = createPayment()
        val processing = payment.startProcessing()
        assertEquals(PaymentStatus.PROCESSING, processing.status)
    }

    @Test
    fun `should transition from PROCESSING to COMPLETED`() {
        val payment = createPayment(PaymentStatus.PROCESSING)
        val completed = payment.complete()
        assertEquals(PaymentStatus.COMPLETED, completed.status)
        assertNotNull(completed.processedAt)
    }

    @Test
    fun `should transition from PROCESSING to FAILED`() {
        val payment = createPayment(PaymentStatus.PROCESSING)
        val failed = payment.fail(PaymentFailureReason.INSUFFICIENT_FUNDS)
        assertEquals(PaymentStatus.FAILED, failed.status)
        assertEquals(PaymentFailureReason.INSUFFICIENT_FUNDS, failed.failureReason)
        assertNotNull(failed.processedAt)
    }

    @Test
    fun `should transition from PENDING to FAILED`() {
        val payment = createPayment(PaymentStatus.PENDING)
        val failed = payment.fail(PaymentFailureReason.TIMEOUT)
        assertEquals(PaymentStatus.FAILED, failed.status)
        assertEquals(PaymentFailureReason.TIMEOUT, failed.failureReason)
    }

    @Test
    fun `should transition from PENDING to EXPIRED`() {
        val payment = createPayment()
        val expired = payment.expire()
        assertEquals(PaymentStatus.EXPIRED, expired.status)
    }

    @Test
    fun `should transition from COMPLETED to REFUNDED`() {
        val payment = createPayment(PaymentStatus.COMPLETED)
        val refunded = payment.refund()
        assertEquals(PaymentStatus.REFUNDED, refunded.status)
    }

    @Test
    fun `should reject startProcessing from COMPLETED`() {
        val payment = createPayment(PaymentStatus.COMPLETED)
        assertThrows(IllegalArgumentException::class.java) { payment.startProcessing() }
    }

    @Test
    fun `should reject complete from PENDING`() {
        val payment = createPayment(PaymentStatus.PENDING)
        assertThrows(IllegalArgumentException::class.java) { payment.complete() }
    }

    @Test
    fun `should reject fail from COMPLETED`() {
        val payment = createPayment(PaymentStatus.COMPLETED)
        assertThrows(IllegalArgumentException::class.java) { payment.fail(PaymentFailureReason.UNKNOWN) }
    }

    @Test
    fun `should reject expire from PROCESSING`() {
        val payment = createPayment(PaymentStatus.PROCESSING)
        assertThrows(IllegalArgumentException::class.java) { payment.expire() }
    }

    @Test
    fun `should reject refund from PENDING`() {
        val payment = createPayment(PaymentStatus.PENDING)
        assertThrows(IllegalArgumentException::class.java) { payment.refund() }
    }

    @Test
    fun `complete payment should set processedAt`() {
        val before = Instant.now()
        val payment = createPayment(PaymentStatus.PROCESSING)
        val completed = payment.complete()
        assertTrue(completed.processedAt != null && !completed.processedAt.isBefore(before))
    }
}
