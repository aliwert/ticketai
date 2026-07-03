package com.ticketa.inventory.mapper

import com.ticketa.inventory.domain.Movie
import com.ticketa.inventory.dto.MovieResponse
import org.springframework.stereotype.Component

@Component
class MovieMapper {
    fun toResponse(movie: Movie): MovieResponse = MovieResponse(
        id = movie.id.toString(),
        title = movie.title,
        description = movie.description,
        genre = movie.genre,
        durationMinutes = movie.durationMinutes,
        rating = movie.rating,
        posterUrl = movie.posterUrl
    )
}
