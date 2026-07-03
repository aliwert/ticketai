package com.ticketa.payment.domain.event

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PaymentFailedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val paymentId: UUID,
    val reservationId: UUID,
    val sessionId: UUID,
    val seatId: UUID,
    val userId: String,
    val amount: BigDecimal,
    val reason: String,
    val timestamp: Instant = Instant.now()
)
