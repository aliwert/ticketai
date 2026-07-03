package com.ticketa.payment.application.service

import com.ticketa.payment.domain.model.Payment
import com.ticketa.payment.domain.model.PaymentFailureReason

data class PaymentResult(
    val success: Boolean,
    val failureReason: PaymentFailureReason? = null,
    val transactionId: String? = null
)

interface PaymentProcessor {
    suspend fun process(payment: Payment): PaymentResult
}
