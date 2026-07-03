package com.ticketa.auth.api.request

data class LogoutRequest(
    val accessToken: String,
    val refreshToken: String? = null
)
