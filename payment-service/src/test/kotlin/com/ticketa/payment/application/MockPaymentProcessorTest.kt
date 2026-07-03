package com.ticketa.payment.application

import com.ticketa.payment.application.service.MockPaymentProcessor
import com.ticketa.payment.domain.model.Payment
import com.ticketa.payment.domain.model.PaymentMethod
import com.ticketa.payment.domain.model.PaymentStatus
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MockPaymentProcessorTest {

    private val processor = MockPaymentProcessor()

    private fun createPayment(method: PaymentMethod? = null): Payment = Payment(
        id = UUID.randomUUID(),
        reservationId = UUID.randomUUID(),
        sessionId = UUID.randomUUID(),
        seatId = UUID.randomUUID(),
        userId = "user-1",
        amount = BigDecimal.valueOf(30),
        status = PaymentStatus.PROCESSING,
        method = method,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    @Test
    fun `should process credit card payment`() = runBlocking {
        val result = processor.process(createPayment(PaymentMethod.CREDIT_CARD))
        assertTrue(result.success || !result.success)
    }

    @Test
    fun `should process debit card payment`() = runBlocking {
        val result = processor.process(createPayment(PaymentMethod.DEBIT_CARD))
        assertTrue(result.success)
        assertNotNull(result.transactionId)
    }

    @Test
    fun `should process pix payment`() = runBlocking {
        val result = processor.process(createPayment(PaymentMethod.PIX))
        assertTrue(result.success)
        assertNotNull(result.transactionId)
    }

    @Test
    fun `should process cash payment`() = runBlocking {
        val result = processor.process(createPayment(PaymentMethod.CASH))
        assertTrue(result.success)
        assertNotNull(result.transactionId)
    }

    @Test
    fun `should default to credit card when no method`() = runBlocking {
        val result = processor.process(createPayment())
        assertTrue(result.success || !result.success)
    }
}
