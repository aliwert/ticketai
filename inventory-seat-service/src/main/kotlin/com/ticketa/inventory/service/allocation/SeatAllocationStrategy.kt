package com.ticketa.inventory.service.allocation

import com.ticketa.inventory.domain.Seat
import com.ticketa.inventory.domain.SeatPreference

data class AllocateRequest(
    val seats: List<Seat>,
    val quantity: Int,
    val preference: SeatPreference?,
    val sessionId: java.util.UUID,
    val userId: String
)

data class AllocationResult(
    val allocatedSeats: List<Seat>
)

interface SeatAllocationStrategy {
    fun allocate(request: AllocateRequest): AllocationResult
}
