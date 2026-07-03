package com.ticketa.inventory.service.allocation

import com.ticketa.inventory.domain.Seat
import com.ticketa.inventory.domain.SeatPreference
import com.ticketa.inventory.domain.exception.InsufficientSeatsException
import com.ticketa.inventory.domain.exception.InvalidSeatCountException
import org.springframework.stereotype.Component

@Component
class CenterSeatAllocationStrategy : SeatAllocationStrategy {

    override fun allocate(request: AllocateRequest): AllocationResult {
        if (request.quantity < 1) {
            throw InvalidSeatCountException("Quantity must be at least 1")
        }

        val available = request.seats.sortedWith(seatComparator(request.preference))

        if (available.size < request.quantity) {
            throw InsufficientSeatsException(request.quantity, available.size)
        }

        val allocated = findBestContiguousBlock(available, request.quantity)
            ?: available.take(request.quantity)

        return AllocationResult(allocated)
    }

    private fun seatComparator(preference: SeatPreference?): Comparator<Seat> {
        return when (preference) {
            SeatPreference.CENTER -> compareBy(
                { scoreCenter(it) },
                { it.rowNumber },
                { it.columnNumber }
            )
            SeatPreference.LEFT -> compareBy<Seat> { it.columnNumber }
                .thenBy { it.rowNumber }
            SeatPreference.RIGHT -> compareByDescending<Seat> { it.columnNumber }
                .thenBy { it.rowNumber }
            SeatPreference.FRONT -> compareBy<Seat> { it.rowNumber }
                .thenBy { it.columnNumber }
            SeatPreference.BACK -> compareByDescending<Seat> { it.rowNumber }
                .thenBy { it.columnNumber }
            null -> compareBy<Seat> { it.rowNumber }
                .thenBy { it.columnNumber }
        }
    }

    private fun scoreCenter(seat: Seat): Double {
        val centerCol = estimateCenterColumn(seat)
        return -Math.abs(seat.columnNumber - centerCol).toDouble()
    }

    private fun estimateCenterColumn(seat: Seat): Int {
        return seat.columnNumber
    }

    private fun findBestContiguousBlock(seats: List<Seat>, quantity: Int): List<Seat>? {
        if (quantity <= 1) return null

        val grouped = seats.groupBy { it.rowNumber }
        var best: List<Seat>? = null
        var bestScore = Double.NEGATIVE_INFINITY

        for ((_, rowSeats) in grouped) {
            val sorted = rowSeats.sortedBy { it.columnNumber }
            val blocks = findContiguousBlocks(sorted, quantity)

            for (block in blocks) {
                val score = block.sumOf { seat ->
                    scoreCenter(seat).toInt()
                }.toDouble()

                if (score > bestScore || best == null) {
                    bestScore = score
                    best = block
                }
            }
        }

        return best
    }

    private fun findContiguousBlocks(seats: List<Seat>, quantity: Int): List<List<Seat>> {
        if (seats.size < quantity) return emptyList()

        val blocks = mutableListOf<List<Seat>>()
        var i = 0
        while (i <= seats.size - quantity) {
            val block = seats.subList(i, i + quantity)
            if (isContiguous(block)) {
                blocks.add(block)
            }
            i++
        }
        return blocks
    }

    private fun isContiguous(block: List<Seat>): Boolean {
        if (block.size <= 1) return true
        for (i in 1 until block.size) {
            if (block[i].columnNumber != block[i - 1].columnNumber + 1) {
                return false
            }
        }
        return true
    }
}
