package com.ticketa.payment.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Payment(
    val id: UUID,
    val reservationId: UUID,
    val sessionId: UUID,
    val seatId: UUID,
    val userId: String,
    val amount: BigDecimal,
    val status: PaymentStatus,
    val method: PaymentMethod? = null,
    val failureReason: PaymentFailureReason? = null,
    val processedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun startProcessing(): Payment {
        require(status == PaymentStatus.PENDING) { "Cannot process payment in state: $status" }
        return copy(status = PaymentStatus.PROCESSING, updatedAt = Instant.now())
    }

    fun complete(): Payment {
        require(status == PaymentStatus.PROCESSING) { "Cannot complete payment in state: $status" }
        return copy(
            status = PaymentStatus.COMPLETED,
            processedAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    fun fail(reason: PaymentFailureReason): Payment {
        require(status == PaymentStatus.PROCESSING || status == PaymentStatus.PENDING) {
            "Cannot fail payment in state: $status"
        }
        return copy(
            status = PaymentStatus.FAILED,
            failureReason = reason,
            processedAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    fun expire(): Payment {
        require(status == PaymentStatus.PENDING) { "Cannot expire payment in state: $status" }
        return copy(
            status = PaymentStatus.EXPIRED,
            updatedAt = Instant.now()
        )
    }

    fun refund(): Payment {
        require(status == PaymentStatus.COMPLETED) { "Cannot refund payment in state: $status" }
        return copy(
            status = PaymentStatus.REFUNDED,
            updatedAt = Instant.now()
        )
    }
}
