package com.ticketa.auth.infrastructure.redis

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenBlacklistService(
    private val redis: ReactiveStringRedisTemplate
) {
    suspend fun blacklist(jti: String, ttlMillis: Long) {
        redis.opsForValue()
            .set("token:blacklist:$jti", "revoked", Duration.ofMillis(ttlMillis))
            .awaitSingleOrNull()
    }

    suspend fun isBlacklisted(jti: String): Boolean {
        return redis.opsForValue()
            .get("token:blacklist:$jti")
            .awaitSingle()
            .let { it == "revoked" }
    }
}
