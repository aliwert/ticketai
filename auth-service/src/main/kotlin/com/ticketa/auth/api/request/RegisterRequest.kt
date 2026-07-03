package com.ticketa.auth.api.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank @field:Email @field:Size(max = 255)
    val email: String,
    @field:NotBlank @field:Size(min = 8, max = 128)
    val password: String
)
