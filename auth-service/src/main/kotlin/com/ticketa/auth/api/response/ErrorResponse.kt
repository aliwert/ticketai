package com.ticketa.auth.api.response

data class ErrorResponse(
    val error: String,
    val message: String,
    val status: Int
)
