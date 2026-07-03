package com.ticketa.inventory.mapper

import com.ticketa.inventory.domain.Session
import com.ticketa.inventory.dto.SessionResponse
import org.springframework.stereotype.Component

@Component
class SessionMapper {
    fun toResponse(session: Session, movieTitle: String, auditoriumName: String): SessionResponse =
        SessionResponse(
            id = session.id.toString(),
            movieId = session.movieId.toString(),
            movieTitle = movieTitle,
            auditoriumId = session.auditoriumId.toString(),
            auditoriumName = auditoriumName,
            startTime = session.startTime.toString(),
            endTime = session.endTime.toString(),
            price = session.price
        )
}
