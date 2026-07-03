package com.ticketa.auth.application.service

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.security.KeyPairGenerator
import java.util.UUID

class JwtTokenServiceTest {

    private lateinit var jwtTokenService: JwtTokenService
    private lateinit var jwkSet: JWKSet

    @BeforeEach
    fun setUp() {
        val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val publicKey = keyPair.public as java.security.interfaces.RSAPublicKey
        val privateKey = keyPair.private as java.security.interfaces.RSAPrivateKey
        val rsaKey = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .algorithm(com.nimbusds.jose.JWSAlgorithm.RS256)
            .build()
        jwkSet = JWKSet(rsaKey)
        val encoder = NimbusJwtEncoder(com.nimbusds.jose.jwk.source.ImmutableJWKSet(jwkSet))

        jwtTokenService = JwtTokenService(
            jwtEncoder = encoder,
            jwkSet = jwkSet,
            accessTokenExp = 900000L,
            issuer = "test-issuer"
        )
    }

    @Test
    fun `generateAccessToken returns valid JWT`() {
        val userId = UUID.randomUUID()
        val token = jwtTokenService.generateAccessToken(userId, "test@example.com", setOf("USER"))

        assertNotNull(token)
        assertTrue(token.split(".").size == 3)
    }

    @Test
    fun `extractJti returns correct value`() {
        val userId = UUID.randomUUID()
        val token = jwtTokenService.generateAccessToken(userId, "test@example.com", setOf("USER"))

        val jti = jwtTokenService.extractJti(token)
        assertNotNull(jti)
    }

    @Test
    fun `extractExpiration returns positive value`() {
        val userId = UUID.randomUUID()
        val token = jwtTokenService.generateAccessToken(userId, "test@example.com", setOf("USER"))

        val exp = jwtTokenService.extractExpiration(token)
        assertNotNull(exp)
        assertTrue(exp!! > 0)
    }

    @Test
    fun `getJwkSetJson returns valid JSON`() {
        val json = jwtTokenService.getJwkSetJson()
        assertNotNull(json)
        assertTrue(json.contains("keys"))
    }
}
