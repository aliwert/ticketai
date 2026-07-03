package com.ticketa.inventory.controller

import com.ticketa.inventory.dto.SessionResponse
import com.ticketa.inventory.mapper.SessionMapper
import com.ticketa.inventory.service.SessionService
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/inventory/sessions", produces = [MediaType.APPLICATION_JSON_VALUE])
class SessionController(
    private val sessionService: SessionService,
    private val sessionMapper: SessionMapper
) {
    @GetMapping("/by-movie/{movieId}")
    fun getSessionsByMovie(@PathVariable movieId: String): Mono<List<SessionResponse>> = mono {
        val sessions = sessionService.getSessionsByMovie(UUID.fromString(movieId))
        sessions.map { session ->
            val movieTitle = sessionService.getMovieTitle(session.movieId)
            val auditoriumName = sessionService.getAuditoriumName(session.auditoriumId)
            sessionMapper.toResponse(session, movieTitle, auditoriumName)
        }
    }

    @GetMapping("/{id}")
    fun getSessionById(@PathVariable id: String): Mono<SessionResponse> = mono {
        val session = sessionService.getSessionById(UUID.fromString(id))
        val movieTitle = sessionService.getMovieTitle(session.movieId)
        val auditoriumName = sessionService.getAuditoriumName(session.auditoriumId)
        sessionMapper.toResponse(session, movieTitle, auditoriumName)
    }
}
