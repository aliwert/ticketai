package com.ticketa.inventory.service

import com.ticketa.inventory.domain.Seat
import com.ticketa.inventory.domain.SeatStatus
import com.ticketa.inventory.domain.exception.SessionNotFoundException
import com.ticketa.inventory.domain.exception.SeatNotFoundException
import com.ticketa.inventory.domain.exception.SeatUnavailableException
import com.ticketa.inventory.persistence.ReservationRepository
import com.ticketa.inventory.persistence.SeatRepository
import com.ticketa.inventory.persistence.SessionRepository
import com.ticketa.inventory.service.locking.SeatLockService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SeatService(
    private val seatRepository: SeatRepository,
    private val sessionRepository: SessionRepository,
    private val reservationRepository: ReservationRepository,
    private val seatLockService: SeatLockService
) {
    suspend fun getSeatsForSession(sessionId: UUID): List<SeatWithStatus> {
        val session = sessionRepository.findById(sessionId)
            ?: throw SessionNotFoundException(sessionId)

        val seats = seatRepository.findByAuditoriumId(session.auditoriumId)
        val reservations = reservationRepository.findBySessionIdAndStatus(sessionId, "LOCKED")
        val booked = reservationRepository.findBySessionIdAndStatus(sessionId, "CONFIRMED")
        val lockedSeatIds = reservations.map { it.seatId }.toSet() +
            booked.map { it.seatId }.toSet()

        return seats.map { entity ->
            val seat = entity.toDomain()
            val status = when {
                lockedSeatIds.contains(seat.id) -> SeatStatus.LOCKED
                else -> SeatStatus.AVAILABLE
            }
            SeatWithStatus(seat, status)
        }
    }

    suspend fun getSeatById(id: UUID): Seat {
        return seatRepository.findById(id)?.toDomain()
            ?: throw SeatNotFoundException(id)
    }

    suspend fun verifySeatAvailable(sessionId: UUID, seatId: UUID) {
        val reservations = reservationRepository.findBySessionIdAndStatus(sessionId, "LOCKED") +
            reservationRepository.findBySessionIdAndStatus(sessionId, "CONFIRMED")

        if (reservations.any { it.seatId == seatId }) {
            throw SeatUnavailableException(sessionId, seatId)
        }
    }
}

data class SeatWithStatus(
    val seat: Seat,
    val status: SeatStatus
)
