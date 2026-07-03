package com.ticketa.payment.infrastructure.persistence

import com.ticketa.payment.domain.model.Payment
import com.ticketa.payment.domain.model.PaymentFailureReason
import com.ticketa.payment.domain.model.PaymentMethod
import com.ticketa.payment.domain.model.PaymentStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("payments")
data class PaymentEntity(
    @Id
    val id: UUID,
    val reservationId: UUID,
    val sessionId: UUID,
    val seatId: UUID,
    val userId: String,
    val amount: BigDecimal,
    val status: String,
    val method: String? = null,
    val failureReason: String? = null,
    val processedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun toDomain(): Payment = Payment(
        id = id,
        reservationId = reservationId,
        sessionId = sessionId,
        seatId = seatId,
        userId = userId,
        amount = amount,
        status = PaymentStatus.valueOf(status),
        method = method?.let { PaymentMethod.valueOf(it) },
        failureReason = failureReason?.let { PaymentFailureReason.valueOf(it) },
        processedAt = processedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(payment: Payment): PaymentEntity = PaymentEntity(
            id = payment.id,
            reservationId = payment.reservationId,
            sessionId = payment.sessionId,
            seatId = payment.seatId,
            userId = payment.userId,
            amount = payment.amount,
            status = payment.status.name,
            method = payment.method?.name,
            failureReason = payment.failureReason?.name,
            processedAt = payment.processedAt,
            createdAt = payment.createdAt,
            updatedAt = payment.updatedAt
        )
    }
}
