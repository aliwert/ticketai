package com.ticketa.inventory.service

import com.ticketa.inventory.domain.Movie
import kotlinx.coroutines.flow.toList
import com.ticketa.inventory.domain.exception.MovieNotFoundException
import com.ticketa.inventory.persistence.MovieRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MovieService(private val movieRepository: MovieRepository) {

    suspend fun getAllMovies(): List<Movie> {
        return movieRepository.findAll().toList().map { it.toDomain() }
    }

    suspend fun getMovieById(id: UUID): Movie {
        val entity = movieRepository.findById(id)
            ?: throw MovieNotFoundException(id)
        return entity.toDomain()
    }
}
