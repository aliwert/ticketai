package com.ticketa.ticket.controller

import com.ticketa.ticket.api.request.VerifyTicketRequest
import com.ticketa.ticket.api.response.TicketResponse
import com.ticketa.ticket.api.response.TicketVerificationResponse
import com.ticketa.ticket.application.service.TicketService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/tickets")
class TicketController(
    private val ticketService: TicketService
) {
    @GetMapping("/{ticketId}")
    suspend fun getTicket(@PathVariable ticketId: UUID): ResponseEntity<TicketResponse> {
        val ticket = ticketService.getTicket(ticketId)
        return ResponseEntity.ok(TicketResponse.from(ticket))
    }

    @GetMapping("/user/{userId}")
    suspend fun getUserTickets(@PathVariable userId: String): ResponseEntity<List<TicketResponse>> {
        val tickets = ticketService.getUserTickets(userId)
        return ResponseEntity.ok(tickets.map { TicketResponse.from(it) })
    }

    @PostMapping("/verify")
    suspend fun verifyTicket(@Valid @RequestBody request: VerifyTicketRequest): ResponseEntity<TicketVerificationResponse> {
        val result = ticketService.verifyTicket(request.ticketId)
        val status = if (result.valid) HttpStatus.OK else HttpStatus.UNPROCESSABLE_ENTITY
        return ResponseEntity.status(status).body(
            TicketVerificationResponse(valid = result.valid, ticketId = result.ticketId, reason = result.reason)
        )
    }
}
