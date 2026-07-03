package com.ticketa.payment.domain.model

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    REFUNDED,
    EXPIRED
}
