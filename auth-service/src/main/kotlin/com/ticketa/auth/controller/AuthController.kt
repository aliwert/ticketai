package com.ticketa.auth.controller

import com.ticketa.auth.api.GlobalExceptionHandler
import com.ticketa.auth.api.request.LoginRequest
import com.ticketa.auth.api.request.LogoutRequest
import com.ticketa.auth.api.request.RefreshTokenRequest
import com.ticketa.auth.api.request.RegisterRequest
import com.ticketa.auth.api.response.TokenResponse
import com.ticketa.auth.application.service.AuthResult
import com.ticketa.auth.application.service.AuthService
import com.ticketa.auth.application.service.JwtTokenService
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenService: JwtTokenService
) {
    @PostMapping("/register")
    suspend fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<TokenResponse> {
        val result: AuthResult = authService.register(request.email, request.password)
        return ResponseEntity.ok(toResponse(result))
    }

    @PostMapping("/login")
    suspend fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val result = authService.login(request.email, request.password)
        return ResponseEntity.ok(toResponse(result))
    }

    @PostMapping("/refresh")
    suspend fun refresh(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<TokenResponse> {
        val result = authService.refresh(request.refreshToken)
        return ResponseEntity.ok(toResponse(result))
    }

    @PostMapping("/logout")
    suspend fun logout(@RequestBody request: LogoutRequest): ResponseEntity<Map<String, String>> {
        authService.logout(request.accessToken, request.refreshToken)
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }

    @GetMapping("/jwks")
    fun jwks(): ResponseEntity<String> {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(jwtTokenService.getJwkSetJson())
    }

    private fun toResponse(result: AuthResult) = TokenResponse(
        accessToken = result.accessToken,
        refreshToken = result.refreshToken,
        expiresIn = result.expiresIn
    )
}
