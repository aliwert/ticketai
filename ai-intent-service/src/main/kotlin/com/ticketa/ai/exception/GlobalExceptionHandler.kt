package com.ticketa.ai.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidation(ex: WebExchangeBindException): Mono<ResponseEntity<Map<String, Any>>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        return Mono.just(
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    mapOf(
                        "timestamp" to Instant.now().toString(),
                        "status" to 400,
                        "error" to "Validation Failed",
                        "fieldErrors" to errors
                    )
                )
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidBody(ex: HttpMessageNotReadableException): Mono<ResponseEntity<Map<String, Any>>> {
        return Mono.just(
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    mapOf(
                        "timestamp" to Instant.now().toString(),
                        "status" to 400,
                        "error" to "Bad Request",
                        "message" to "Request body is missing required fields or contains invalid data"
                    )
                )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): Mono<ResponseEntity<Map<String, Any>>> {
        return Mono.just(
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    mapOf(
                        "timestamp" to Instant.now().toString(),
                        "status" to 500,
                        "error" to "Internal Server Error",
                        "message" to (ex.message ?: "An unexpected error occurred")
                    )
                )
        )
    }
}
