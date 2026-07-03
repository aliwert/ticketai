package com.ticketa.payment.domain.exception

import java.util.UUID

sealed class PaymentException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class PaymentNotFoundException(id: UUID) : PaymentException("Payment not found: $id")

class InvalidPaymentStateException(message: String) : PaymentException(message)

class DuplicateEventException(eventId: String) : PaymentException("Duplicate event: $eventId")

class PaymentProcessingException(message: String, cause: Throwable? = null) : PaymentException(message, cause)
