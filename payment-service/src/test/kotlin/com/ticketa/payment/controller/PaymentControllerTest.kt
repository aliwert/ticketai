package com.ticketa.payment.controller

import com.ticketa.payment.application.service.PaymentService
import com.ticketa.payment.domain.model.Payment
import com.ticketa.payment.domain.model.PaymentStatus
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentControllerTest {

    private val paymentService: PaymentService = mock()
    private val controller = PaymentController(paymentService)

    @Test
    fun `should return payment by id`() = runBlocking {
        val paymentId = UUID.randomUUID()
        val payment = Payment(
            id = paymentId,
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = "user-1",
            amount = BigDecimal.valueOf(30),
            status = PaymentStatus.PENDING,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { paymentService.getPayment(paymentId) } doReturn payment

        val response = controller.getPayment(paymentId)
        assertEquals(200, response.statusCode.value())
        assertNotNull(response.body)
        assertEquals(paymentId, response.body!!.id)
    }

    @Test
    fun `should return payment status`() = runBlocking {
        val paymentId = UUID.randomUUID()
        wheneverBlocking { paymentService.getPaymentStatus(paymentId) } doReturn PaymentStatus.COMPLETED

        val response = controller.getPaymentStatus(paymentId)
        assertEquals(200, response.statusCode.value())
        assertEquals(PaymentStatus.COMPLETED, response.body!!.status)
    }

    @Test
    fun `should return user payments`() = runBlocking {
        val userId = "user-1"
        val payment = Payment(
            id = UUID.randomUUID(),
            reservationId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID(),
            userId = userId,
            amount = BigDecimal.valueOf(30),
            status = PaymentStatus.PENDING,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { paymentService.getUserPayments(userId) } doReturn listOf(payment)

        val response = controller.getUserPayments(userId)
        assertEquals(200, response.statusCode.value())
        assertEquals(1, response.body!!.size)
        assertEquals(userId, response.body!![0].userId)
    }
}
