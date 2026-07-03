package com.ticketa.gateway.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Instant

@RestController
@RequestMapping("/fallback")
class FallbackController {

    @GetMapping("/{service}")
    fun fallback(@PathVariable service: String): Mono<ResponseEntity<Map<String, *>>> {
        return Mono.just(
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                    mapOf(
                        "timestamp" to Instant.now().toString(),
                        "status" to 503,
                        "error" to "Service Unavailable",
                        "message" to "$service service is currently unavailable. Please try again later.",
                        "service" to service
                    )
                )
        )
    }

    @GetMapping("/auth")
    fun authFallback() = fallback("auth")

    @GetMapping("/intent")
    fun intentFallback() = fallback("intent")

    @GetMapping("/inventory")
    fun inventoryFallback() = fallback("inventory")

    @GetMapping("/payment")
    fun paymentFallback() = fallback("payment")

    @GetMapping("/ticket")
    fun ticketFallback() = fallback("ticket")

    @GetMapping("/notification")
    fun notificationFallback() = fallback("notification")
}
