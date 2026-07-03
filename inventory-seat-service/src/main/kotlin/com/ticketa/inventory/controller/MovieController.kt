package com.ticketa.inventory.controller

import com.ticketa.inventory.dto.MovieResponse
import com.ticketa.inventory.mapper.MovieMapper
import com.ticketa.inventory.service.MovieService
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/inventory/movies", produces = [MediaType.APPLICATION_JSON_VALUE])
class MovieController(
    private val movieService: MovieService,
    private val movieMapper: MovieMapper
) {
    @GetMapping
    fun getAllMovies(): Mono<List<MovieResponse>> = mono {
        movieService.getAllMovies().map { movieMapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun getMovieById(@PathVariable id: String): Mono<MovieResponse> = mono {
        val movie = movieService.getMovieById(UUID.fromString(id))
        movieMapper.toResponse(movie)
    }
}
