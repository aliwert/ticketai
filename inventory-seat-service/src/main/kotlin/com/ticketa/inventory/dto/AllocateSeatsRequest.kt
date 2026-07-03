package com.ticketa.inventory.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class AllocateSeatsRequest(
    @field:NotBlank
    val sessionId: String,

    @field:Min(1)
    val quantity: Int,

    val preference: String? = null
)
