package com.ticketa.inventory.dto

data class MovieResponse(
    val id: String,
    val title: String,
    val description: String?,
    val genre: String?,
    val durationMinutes: Int,
    val rating: String?,
    val posterUrl: String?
)
