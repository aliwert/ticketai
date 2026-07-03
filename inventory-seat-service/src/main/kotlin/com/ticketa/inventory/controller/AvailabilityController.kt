package com.ticketa.inventory.controller

import com.ticketa.inventory.mapper.SeatMapper
import com.ticketa.inventory.service.SeatService
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/inventory/availability", produces = [MediaType.APPLICATION_JSON_VALUE])
class AvailabilityController(
    private val seatService: SeatService,
    private val seatMapper: SeatMapper
) {
    @GetMapping("/session/{sessionId}")
    fun getSessionAvailability(@PathVariable sessionId: String): Mono<Map<String, Any>> = mono {
        val seats = seatService.getSeatsForSession(UUID.fromString(sessionId))
        val available = seats.count { it.status.name == "AVAILABLE" }
        val locked = seats.count { it.status.name == "LOCKED" }
        mapOf(
            "sessionId" to sessionId,
            "totalSeats" to seats.size,
            "availableSeats" to available,
            "lockedSeats" to locked,
            "seats" to seats.map { seatMapper.toResponse(it.seat, it.status) }
        )
    }
}
