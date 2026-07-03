package com.ticketa.ticket.exception

import com.ticketa.ticket.domain.exception.DuplicateEventException
import com.ticketa.ticket.domain.exception.InvalidTicketStateException
import com.ticketa.ticket.domain.exception.TicketNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(TicketNotFoundException::class)
    fun handleTicketNotFound(ex: TicketNotFoundException): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message!!)
        problem.title = "Ticket Not Found"
        return problem
    }

    @ExceptionHandler(InvalidTicketStateException::class)
    fun handleInvalidTicketState(ex: InvalidTicketStateException): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message!!)
        problem.title = "Invalid Ticket State"
        return problem
    }

    @ExceptionHandler(DuplicateEventException::class)
    fun handleDuplicateEvent(ex: DuplicateEventException): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message!!)
        problem.title = "Duplicate Event"
        return problem
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message!!)
        problem.title = "Bad Request"
        return problem
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ProblemDetail {
        log.error("Unhandled exception", ex)
        val problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        )
        problem.title = "Internal Server Error"
        return problem
    }
}
