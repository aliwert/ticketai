package com.ticketa.inventory.exception

import com.ticketa.inventory.domain.exception.InsufficientSeatsException
import com.ticketa.inventory.domain.exception.InvalidSeatCountException
import com.ticketa.inventory.domain.exception.LockNotAcquiredException
import com.ticketa.inventory.domain.exception.MovieNotFoundException
import com.ticketa.inventory.domain.exception.ReservationNotFoundException
import com.ticketa.inventory.domain.exception.SeatNotFoundException
import com.ticketa.inventory.domain.exception.SeatUnavailableException
import com.ticketa.inventory.domain.exception.SessionNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MovieNotFoundException::class)
    fun handleMovieNotFound(ex: MovieNotFoundException) = notFound(ex.message)

    @ExceptionHandler(SessionNotFoundException::class)
    fun handleSessionNotFound(ex: SessionNotFoundException) = notFound(ex.message)

    @ExceptionHandler(SeatNotFoundException::class)
    fun handleSeatNotFound(ex: SeatNotFoundException) = notFound(ex.message)

    @ExceptionHandler(ReservationNotFoundException::class)
    fun handleReservationNotFound(ex: ReservationNotFoundException) = notFound(ex.message)

    @ExceptionHandler(SeatUnavailableException::class)
    fun handleSeatUnavailable(ex: SeatUnavailableException) =
        error(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(InsufficientSeatsException::class)
    fun handleInsufficientSeats(ex: InsufficientSeatsException) =
        error(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(LockNotAcquiredException::class)
    fun handleLockNotAcquired(ex: LockNotAcquiredException) =
        error(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(InvalidSeatCountException::class)
    fun handleInvalidSeatCount(ex: InvalidSeatCountException) =
        error(HttpStatus.BAD_REQUEST, ex.message)

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidation(ex: WebExchangeBindException): Mono<ResponseEntity<Map<String, Any>>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        return Mono.just(
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf(
                    "timestamp" to Instant.now().toString(),
                    "status" to 400,
                    "error" to "Validation Failed",
                    "fieldErrors" to errors
                ))
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): Mono<ResponseEntity<Map<String, Any>>> {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
    }

    private fun notFound(message: String?): Mono<ResponseEntity<Map<String, Any>>> =
        error(HttpStatus.NOT_FOUND, message)

    private fun error(status: HttpStatus, message: String?): Mono<ResponseEntity<Map<String, Any>>> =
        Mono.just(
            ResponseEntity.status(status)
                .body(mapOf(
                    "timestamp" to Instant.now().toString(),
                    "status" to status.value(),
                    "error" to status.reasonPhrase,
                    "message" to (message ?: "Unknown error")
                ))
        )
}
