package com.ticketa.inventory.service

import com.ticketa.inventory.domain.Session
import com.ticketa.inventory.domain.exception.SessionNotFoundException
import com.ticketa.inventory.persistence.AuditoriumRepository
import com.ticketa.inventory.persistence.MovieRepository
import com.ticketa.inventory.persistence.SessionEntity
import com.ticketa.inventory.persistence.SessionRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
    private val movieRepository: MovieRepository,
    private val auditoriumRepository: AuditoriumRepository
) {
    suspend fun getSessionsByMovie(movieId: UUID): List<Session> {
        return sessionRepository.findByMovieId(movieId).map { it.toDomain() }
    }

    suspend fun getSessionById(id: UUID): Session {
        val entity = sessionRepository.findById(id)
            ?: throw SessionNotFoundException(id)
        return entity.toDomain()
    }

    suspend fun getMovieTitle(movieId: UUID): String {
        return movieRepository.findById(movieId)?.title ?: "Unknown"
    }

    suspend fun getAuditoriumName(auditoriumId: UUID): String {
        return auditoriumRepository.findById(auditoriumId)?.name ?: "Unknown"
    }
}
