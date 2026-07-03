package com.ticketa.payment.domain.event

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PaymentCompletedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val paymentId: UUID,
    val reservationId: UUID,
    val sessionId: UUID,
    val seatId: UUID,
    val userId: String,
    val amount: BigDecimal,
    val timestamp: Instant = Instant.now()
)
