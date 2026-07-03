package com.ticketa.inventory.domain.exception

sealed class InventoryException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class MovieNotFoundException(id: Any) : InventoryException("Movie not found: $id")
class SessionNotFoundException(id: Any) : InventoryException("Session not found: $id")
class SeatNotFoundException(id: Any) : InventoryException("Seat not found: $id")
class SeatUnavailableException(sessionId: Any, seatId: Any) :
    InventoryException("Seat $seatId is unavailable for session $sessionId")
class ReservationNotFoundException(id: Any) : InventoryException("Reservation not found: $id")
class InsufficientSeatsException(requested: Int, available: Int) :
    InventoryException("Requested $requested seats but only $available available")
class LockNotAcquiredException(lockKey: String) :
    InventoryException("Could not acquire lock: $lockKey")
class LockNotOwnedException(lockKey: String) :
    InventoryException("Does not own lock: $lockKey")
class InvalidSeatCountException(message: String) : InventoryException(message)
class InvalidSessionTimeException(message: String) : InventoryException(message)
