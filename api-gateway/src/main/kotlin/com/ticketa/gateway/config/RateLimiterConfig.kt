package com.ticketa.gateway.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration
class RateLimiterConfig {

    @Bean
    fun userKeyResolver(): KeyResolver {
        return KeyResolver { exchange ->
            val userId = exchange.request.headers.getFirst("X-User-Id")
            val remoteAddr = exchange.request.remoteAddress?.address?.hostAddress
            Mono.just(userId ?: remoteAddr ?: "anonymous")
        }
    }
}
