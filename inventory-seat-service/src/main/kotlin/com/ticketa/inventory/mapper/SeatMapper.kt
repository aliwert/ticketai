package com.ticketa.inventory.mapper

import com.ticketa.inventory.domain.Seat
import com.ticketa.inventory.domain.SeatStatus
import com.ticketa.inventory.dto.SeatResponse
import org.springframework.stereotype.Component

@Component
class SeatMapper {
    fun toResponse(seat: Seat, status: SeatStatus): SeatResponse = SeatResponse(
        id = seat.id.toString(),
        rowNumber = seat.rowNumber,
        columnNumber = seat.columnNumber,
        seatLabel = seat.seatLabel,
        seatType = seat.seatType.name,
        status = status.name
    )
}
