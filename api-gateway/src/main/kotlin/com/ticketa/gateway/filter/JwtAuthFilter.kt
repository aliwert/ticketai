package com.ticketa.gateway.filter

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class JwtAuthFilter(
    @Value("\${app.jwt.jwks-url}") private val jwksUrl: String,
    @Value("\${app.jwt.cache-ttl-seconds}") private val cacheTtl: Long
) : GlobalFilter {

    private val log = LoggerFactory.getLogger(JwtAuthFilter::class.java)
    private val webClient = WebClient.create()
    private val cachedProcessor = AtomicReference<CachedProcessor>()

    private val publicPrefixes = setOf(
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/auth/jwks"
    )

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val path = exchange.request.uri.path

        if (isPublicPath(path)) {
            return chain.filter(exchange)
        }

        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            return exchange.response.setComplete()
        }

        val token = authHeader.removePrefix("Bearer ")

        return getJwtProcessor()
            .flatMap { processor ->
                Mono.fromCallable {
                    processor.process(token, null as SecurityContext?)
                }
            }
            .flatMap { claims ->
                val userId = claims.subject
                val roles = claims.getStringListClaim("roles")?.joinToString(",") ?: ""

                val mutatedRequest = exchange.request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Roles", roles)
                    .build()

                val mutatedExchange = exchange.mutate().request(mutatedRequest).build()
                chain.filter(mutatedExchange)
            }
            .onErrorResume { e ->
                log.warn("JWT validation failed for path={}: {}", path, e.message)
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                exchange.response.setComplete()
            }
    }

    internal fun isPublicPath(path: String): Boolean {
        val lowered = path.lowercase()
        return publicPrefixes.any { lowered.startsWith(it) }
    }

    private fun getJwtProcessor(): Mono<DefaultJWTProcessor<SecurityContext>> {
        val cached = cachedProcessor.get()
        if (cached != null && cached.expiresAt.isAfter(Instant.now())) {
            return Mono.just(cached.processor)
        }

        return webClient.get()
            .uri(jwksUrl)
            .retrieve()
            .bodyToMono(String::class.java)
            .map { json ->
                val jwkSet = JWKSet.parse(json)
                val processor = DefaultJWTProcessor<SecurityContext>()
                val keySelector = JWSVerificationKeySelector(
                    JWSAlgorithm.RS256,
                    ImmutableJWKSet(jwkSet)
                )
                processor.jwsKeySelector = keySelector
                val entry = CachedProcessor(processor, Instant.now().plusSeconds(cacheTtl))
                cachedProcessor.set(entry)
                processor
            }
    }

    private data class CachedProcessor(
        val processor: DefaultJWTProcessor<SecurityContext>,
        val expiresAt: Instant
    )
}
