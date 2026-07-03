package com.ticketa.inventory.service.locking

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingleOrNull

@Service
class RedisSeatLockService(
    private val redis: ReactiveStringRedisTemplate
) : SeatLockService {

    private val log = LoggerFactory.getLogger(RedisSeatLockService::class.java)

    companion object {
        private const val LOCK_KEY_PREFIX = "seat-lock:"
        private val UNLOCK_SCRIPT = DefaultRedisScript(
            """
                if redis.call("GET", KEYS[1]) == ARGV[1] then
                    return redis.call("DEL", KEYS[1])
                else
                    return 0
                end
            """.trimIndent(),
            Long::class.java
        )
    }

    override suspend fun acquireLock(sessionId: UUID, seatId: UUID, ownerId: String, ttlMs: Long): LockResult {
        val key = lockKey(sessionId, seatId)
        return try {
            val result = redis.opsForValue()
                .setIfAbsent(key, ownerId, Duration.ofMillis(ttlMs))
                .awaitSingleOrNull() ?: false

            if (result) {
                log.debug("Lock acquired: key={}, owner={}", key, ownerId)
                LockResult(true, ownerId)
            } else {
                log.debug("Lock not acquired: key={}, owner={}", key, ownerId)
                LockResult(false, "")
            }
        } catch (e: Exception) {
            log.error("Redis error acquiring lock: key={}", key, e)
            LockResult(false, "")
        }
    }

    override suspend fun releaseLock(sessionId: UUID, seatId: UUID, ownerId: String): Boolean {
        val key = lockKey(sessionId, seatId)
        return try {
            val result = redis.execute(
                UNLOCK_SCRIPT,
                listOf(key),
                listOf(ownerId)
            ).next().awaitSingleOrNull() ?: 0L

            val released = result == 1L
            if (released) {
                log.debug("Lock released: key={}, owner={}", key, ownerId)
            } else {
                log.warn("Lock release failed (not owner): key={}, owner={}", key, ownerId)
            }
            released
        } catch (e: Exception) {
            log.error("Redis error releasing lock: key={}", key, e)
            false
        }
    }

    override suspend fun isLocked(sessionId: UUID, seatId: UUID): Boolean {
        val key = lockKey(sessionId, seatId)
        return try {
            val value = redis.opsForValue().get(key).awaitSingleOrNull()
            value != null
        } catch (e: Exception) {
            log.error("Redis error checking lock: key={}", key, e)
            false
        }
    }

    override suspend fun verifyOwnership(sessionId: UUID, seatId: UUID, ownerId: String): Boolean {
        val key = lockKey(sessionId, seatId)
        return try {
            val value = redis.opsForValue().get(key).awaitSingleOrNull()
            ownerId == value
        } catch (e: Exception) {
            log.error("Redis error verifying ownership: key={}", key, e)
            false
        }
    }

    private fun lockKey(sessionId: UUID, seatId: UUID): String =
        "$LOCK_KEY_PREFIX$sessionId:$seatId"
}
