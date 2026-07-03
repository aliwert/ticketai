package com.ticketa.payment.domain.model

enum class PaymentFailureReason {
    INSUFFICIENT_FUNDS,
    CARD_DECLINED,
    EXPIRED_CARD,
    INVALID_CVV,
    TIMEOUT,
    PROCESSING_ERROR,
    UNKNOWN
}
