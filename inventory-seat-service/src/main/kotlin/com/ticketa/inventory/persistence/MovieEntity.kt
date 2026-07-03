package com.ticketa.inventory.persistence

import com.ticketa.inventory.domain.Movie
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("movies")
data class MovieEntity(
    @Id val id: UUID,
    val title: String,
    val description: String?,
    val genre: String?,
    @Column("duration_minutes") val durationMinutes: Int,
    val rating: String?,
    @Column("poster_url") val posterUrl: String?,
    @Column("created_at") val createdAt: Instant,
    @Column("updated_at") val updatedAt: Instant
) {
    fun toDomain(): Movie = Movie(
        id = id, title = title, description = description, genre = genre,
        durationMinutes = durationMinutes, rating = rating, posterUrl = posterUrl,
        createdAt = createdAt, updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(movie: Movie): MovieEntity = MovieEntity(
            id = movie.id, title = movie.title, description = movie.description,
            genre = movie.genre, durationMinutes = movie.durationMinutes,
            rating = movie.rating, posterUrl = movie.posterUrl,
            createdAt = movie.createdAt, updatedAt = movie.updatedAt
        )
    }
}
