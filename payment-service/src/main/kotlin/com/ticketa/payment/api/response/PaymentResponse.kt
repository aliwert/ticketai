package com.ticketa.payment.api.response

import com.ticketa.payment.domain.model.Payment
import com.ticketa.payment.domain.model.PaymentFailureReason
import com.ticketa.payment.domain.model.PaymentMethod
import com.ticketa.payment.domain.model.PaymentStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PaymentResponse(
    val id: UUID,
    val reservationId: UUID,
    val sessionId: UUID,
    val seatId: UUID,
    val userId: String,
    val amount: BigDecimal,
    val status: PaymentStatus,
    val method: PaymentMethod?,
    val failureReason: PaymentFailureReason?,
    val processedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(payment: Payment): PaymentResponse = PaymentResponse(
            id = payment.id,
            reservationId = payment.reservationId,
            sessionId = payment.sessionId,
            seatId = payment.seatId,
            userId = payment.userId,
            amount = payment.amount,
            status = payment.status,
            method = payment.method,
            failureReason = payment.failureReason,
            processedAt = payment.processedAt,
            createdAt = payment.createdAt,
            updatedAt = payment.updatedAt
        )
    }
}

data class PaymentStatusResponse(
    val id: UUID,
    val status: PaymentStatus
)
