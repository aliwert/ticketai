package com.ticketa.inventory.service

import com.ticketa.inventory.domain.Reservation
import com.ticketa.inventory.domain.ReservationStatus
import com.ticketa.inventory.domain.SeatPreference
import com.ticketa.inventory.domain.exception.InsufficientSeatsException
import com.ticketa.inventory.domain.exception.LockNotAcquiredException
import com.ticketa.inventory.domain.exception.ReservationNotFoundException
import com.ticketa.inventory.domain.exception.SessionNotFoundException
import com.ticketa.inventory.persistence.ReservationEntity
import com.ticketa.inventory.persistence.ReservationRepository
import com.ticketa.inventory.persistence.SessionRepository
import com.ticketa.inventory.service.allocation.AllocateRequest
import com.ticketa.inventory.service.allocation.CenterSeatAllocationStrategy
import com.ticketa.inventory.service.locking.SeatLockService
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val sessionRepository: SessionRepository,
    private val seatService: SeatService,
    private val allocationStrategy: CenterSeatAllocationStrategy,
    private val seatLockService: SeatLockService
) {
    suspend fun allocateSeats(
        sessionId: UUID,
        quantity: Int,
        preference: SeatPreference?,
        userId: String
    ): List<Reservation> {
        val session = sessionRepository.findById(sessionId)
            ?: throw SessionNotFoundException(sessionId)

        val seatsWithStatus = seatService.getSeatsForSession(sessionId)
        val availableSeats = seatsWithStatus
            .filter { it.status.name == "AVAILABLE" }
            .map { it.seat }

        if (availableSeats.size < quantity) {
            throw InsufficientSeatsException(quantity, availableSeats.size)
        }

        val request = AllocateRequest(
            seats = availableSeats,
            quantity = quantity,
            preference = preference,
            sessionId = sessionId,
            userId = userId
        )
        val result = allocationStrategy.allocate(request)

        val reservations = mutableListOf<Reservation>()
        for (seat in result.allocatedSeats) {
            val ownerId = UUID.randomUUID().toString()
            val lockResult = seatLockService.acquireLock(sessionId, seat.id, ownerId)

            if (!lockResult.acquired) {
                reservations.forEach { r ->
                    seatLockService.releaseLock(sessionId, r.seatId, lockResult.ownerId.ifEmpty { ownerId })
                }
                throw LockNotAcquiredException("seat-lock:$sessionId:${seat.id}")
            }

            val ttlMs = 30000L
            val expiresAt = Instant.now().plusMillis(ttlMs)
            val entity = ReservationEntity(
                id = UUID.randomUUID(),
                sessionId = sessionId,
                seatId = seat.id,
                userId = userId,
                status = ReservationStatus.LOCKED.name,
                lockedAt = Instant.now(),
                expiresAt = expiresAt,
                confirmedAt = null,
                cancelledAt = null,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            val saved = reservationRepository.save(entity)
            reservations.add(saved.toDomain())
        }

        return reservations
    }

    suspend fun getReservation(id: UUID): Reservation {
        return reservationRepository.findById(id)?.toDomain()
            ?: throw ReservationNotFoundException(id)
    }

    suspend fun getReservationsByUser(userId: String): List<Reservation> {
        return reservationRepository.findByUserIdAndStatus(userId, "LOCKED")
            .map { it.toDomain() }
    }
}
