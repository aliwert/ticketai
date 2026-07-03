package com.ticketa.payment.controller

import com.ticketa.payment.api.response.PaymentResponse
import com.ticketa.payment.api.response.PaymentStatusResponse
import com.ticketa.payment.application.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {
    @GetMapping("/{paymentId}")
    suspend fun getPayment(@PathVariable paymentId: UUID): ResponseEntity<PaymentResponse> {
        val payment = paymentService.getPayment(paymentId)
        return ResponseEntity.ok(PaymentResponse.from(payment))
    }

    @GetMapping("/{paymentId}/status")
    suspend fun getPaymentStatus(@PathVariable paymentId: UUID): ResponseEntity<PaymentStatusResponse> {
        val status = paymentService.getPaymentStatus(paymentId)
        return ResponseEntity.ok(PaymentStatusResponse(id = paymentId, status = status))
    }

    @GetMapping
    suspend fun getUserPayments(@RequestParam userId: String): ResponseEntity<List<PaymentResponse>> {
        val payments = paymentService.getUserPayments(userId)
        return ResponseEntity.ok(payments.map { PaymentResponse.from(it) })
    }
}
