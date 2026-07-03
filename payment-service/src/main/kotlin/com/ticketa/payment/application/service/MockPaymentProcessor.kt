package com.ticketa.payment.application.service

import com.ticketa.payment.domain.model.Payment
import com.ticketa.payment.domain.model.PaymentFailureReason
import com.ticketa.payment.domain.model.PaymentMethod
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MockPaymentProcessor : PaymentProcessor {

    private val log = LoggerFactory.getLogger(MockPaymentProcessor::class.java)

    override suspend fun process(payment: Payment): PaymentResult {
        log.info("Processing payment: id={}, amount={}, method={}", payment.id, payment.amount, payment.method)

        val method = payment.method ?: PaymentMethod.CREDIT_CARD

        return when (method) {
            PaymentMethod.CREDIT_CARD -> simulateCreditCard(payment)
            PaymentMethod.DEBIT_CARD -> simulateDebitCard(payment)
            PaymentMethod.PIX -> simulatePix(payment)
            PaymentMethod.CASH -> simulateCash(payment)
        }
    }

    private suspend fun simulateCreditCard(payment: Payment): PaymentResult {
        delay(500)
        val lastFour = payment.id.toString().takeLast(4)
        return if ((lastFour.first().digitToIntOrNull(16) ?: 0) > 8) {
            PaymentResult(
                success = false,
                failureReason = PaymentFailureReason.CARD_DECLINED
            )
        } else {
            PaymentResult(
                success = true,
                transactionId = "CC-${UUID.randomUUID()}"
            )
        }
    }

    private suspend fun simulateDebitCard(payment: Payment): PaymentResult {
        delay(800)
        return PaymentResult(
            success = true,
            transactionId = "DC-${UUID.randomUUID()}"
        )
    }

    private suspend fun simulatePix(payment: Payment): PaymentResult {
        delay(200)
        return PaymentResult(
            success = true,
            transactionId = "PIX-${UUID.randomUUID()}"
        )
    }

    private suspend fun simulateCash(payment: Payment): PaymentResult {
        return PaymentResult(
            success = true,
            transactionId = "CSH-${UUID.randomUUID()}"
        )
    }
}
