package com.ticketa.inventory.controller

import com.ticketa.inventory.domain.SeatPreference
import com.ticketa.inventory.domain.SeatStatus
import com.ticketa.inventory.dto.AllocateSeatsRequest
import com.ticketa.inventory.dto.AllocateSeatsResponse
import com.ticketa.inventory.dto.SeatMapResponse
import com.ticketa.inventory.mapper.SeatMapper
import com.ticketa.inventory.service.ReservationService
import com.ticketa.inventory.service.SeatService
import jakarta.validation.Valid
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/inventory/seats", produces = [MediaType.APPLICATION_JSON_VALUE])
class SeatController(
    private val seatService: SeatService,
    private val reservationService: ReservationService,
    private val seatMapper: SeatMapper
) {
    @GetMapping("/map/{sessionId}")
    fun getSeatMap(@PathVariable sessionId: String): Mono<SeatMapResponse> = mono {
        val seats = seatService.getSeatsForSession(UUID.fromString(sessionId))
        SeatMapResponse(
            sessionId = sessionId,
            auditoriumName = "",
            totalRows = seats.maxOfOrNull { it.seat.rowNumber } ?: 0,
            totalColumns = seats.maxOfOrNull { it.seat.columnNumber } ?: 0,
            seats = seats.map { seatMapper.toResponse(it.seat, it.status) }
        )
    }

    @PostMapping("/allocate")
    fun allocateSeats(
        @Valid @RequestBody request: AllocateSeatsRequest,
        @RequestHeader("X-User-Id") userId: String
    ): Mono<AllocateSeatsResponse> = mono {
        val preference = request.preference?.let {
            try {
                SeatPreference.valueOf(it.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        val reservations = reservationService.allocateSeats(
            sessionId = UUID.fromString(request.sessionId),
            quantity = request.quantity,
            preference = preference,
            userId = userId
        )

        val first = reservations.first()
        AllocateSeatsResponse(
            reservationId = first.id.toString(),
            sessionId = first.sessionId.toString(),
            seats = reservations.map { res ->
                val seat = seatService.getSeatById(res.seatId)
                seatMapper.toResponse(seat, SeatStatus.LOCKED)
            },
            expiresAt = first.expiresAt.toString()
        )
    }
}
