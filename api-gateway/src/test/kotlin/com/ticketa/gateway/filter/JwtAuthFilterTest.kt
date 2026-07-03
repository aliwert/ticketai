package com.ticketa.gateway.filter

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class JwtAuthFilterTest {

    private val webClientBuilder = WebClient.builder()
    private val filter = JwtAuthFilter("http://localhost:8081/api/auth/jwks", 300, webClientBuilder)

    @Test
    fun `public path register bypasses auth`() {
        assertTrue(filter.isPublicPath("/api/auth/register"))
    }

    @Test
    fun `public path login bypasses auth`() {
        assertTrue(filter.isPublicPath("/api/auth/login"))
    }

    @Test
    fun `public path refresh bypasses auth`() {
        assertTrue(filter.isPublicPath("/api/auth/refresh"))
    }

    @Test
    fun `public path jwks bypasses auth`() {
        assertTrue(filter.isPublicPath("/api/auth/jwks"))
    }

    @Test
    fun `inventory path requires auth`() {
        assertFalse(filter.isPublicPath("/api/inventory/movies"))
    }

    @Test
    fun `intent path requires auth`() {
        assertFalse(filter.isPublicPath("/api/intent/extract"))
    }

    @Test
    fun `payment path requires auth`() {
        assertFalse(filter.isPublicPath("/api/payments/create"))
    }

    @Test
    fun `ticket path requires auth`() {
        assertFalse(filter.isPublicPath("/api/tickets/123"))
    }

    @Test
    fun `notification path requires auth`() {
        assertFalse(filter.isPublicPath("/api/notifications/send"))
    }

    @Test
    fun `logout path requires auth`() {
        assertFalse(filter.isPublicPath("/api/auth/logout"))
    }

    @Test
    fun `public path check is case insensitive`() {
        assertTrue(filter.isPublicPath("/API/AUTH/REGISTER"))
    }

    @Test
    fun `path with trailing slash matches`() {
        assertTrue(filter.isPublicPath("/api/auth/register/"))
    }
}
