package com.ticketa.auth.application.service

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class JwtTokenService(
    private val jwtEncoder: NimbusJwtEncoder,
    private val jwkSet: JWKSet,
    @Value("\${app.jwt.access-token-expiration}") private val accessTokenExp: Long,
    @Value("\${app.issuer}") private val issuer: String
) {
    fun generateAccessToken(userId: UUID, email: String, roles: Set<String>): String {
        val now = Instant.now()
        val jti = UUID.randomUUID().toString()
        val claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .subject(userId.toString())
            .issuedAt(now)
            .expiresAt(now.plusMillis(accessTokenExp))
            .id(jti)
            .claim("email", email)
            .claim("roles", roles)
            .build()

        val parameters = JwtEncoderParameters.from(claims)
        return jwtEncoder.encode(parameters).tokenValue
    }

    fun getJwkSetJson(): String = jwkSet.toPublicJWKSet().toString()

    fun extractJti(token: String): String? {
        return try {
            val claims = SignedJWT.parse(token).jwtClaimsSet
            claims.getJWTID()
        } catch (_: Exception) {
            null
        }
    }

    fun extractExpiration(token: String): Long? {
        return try {
            val claims = SignedJWT.parse(token).jwtClaimsSet
            val exp = claims.expirationTime
            if (exp != null) exp.time else claims.issueTime?.time?.let { it + accessTokenExp }
        } catch (_: Exception) {
            null
        }
    }
}
