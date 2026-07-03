package com.ticketa.auth.application.service

import com.ticketa.auth.domain.exception.EmailAlreadyExistsException
import com.ticketa.auth.domain.exception.InvalidCredentialsException
import com.ticketa.auth.domain.exception.InvalidRefreshTokenException
import com.ticketa.auth.infrastructure.persistence.RefreshTokenEntity
import com.ticketa.auth.infrastructure.persistence.RefreshTokenRepository
import com.ticketa.auth.infrastructure.persistence.UserEntity
import com.ticketa.auth.infrastructure.persistence.UserRepository
import com.ticketa.auth.infrastructure.redis.TokenBlacklistService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Instant
import java.util.UUID

class AuthServiceTest {

    private lateinit var authService: AuthService
    private lateinit var userRepository: UserRepository
    private lateinit var refreshTokenRepository: RefreshTokenRepository
    private lateinit var tokenBlacklistService: TokenBlacklistService

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        refreshTokenRepository = mock()
        tokenBlacklistService = mock()
        val passwordEncoder = BCryptPasswordEncoder()

        val keyPair = java.security.KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val publicKey = keyPair.public as java.security.interfaces.RSAPublicKey
        val privateKey = keyPair.private as java.security.interfaces.RSAPrivateKey
        val rsaKey = com.nimbusds.jose.jwk.RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .algorithm(com.nimbusds.jose.JWSAlgorithm.RS256)
            .build()
        val jwkSet = com.nimbusds.jose.jwk.JWKSet(rsaKey)
        val encoder = org.springframework.security.oauth2.jwt.NimbusJwtEncoder(
            com.nimbusds.jose.jwk.source.ImmutableJWKSet(jwkSet)
        )
        val jwtTokenService = JwtTokenService(
            jwtEncoder = encoder,
            jwkSet = jwkSet,
            accessTokenExp = 900000L,
            issuer = "test-issuer"
        )

        authService = AuthService(
            userRepository = userRepository,
            refreshTokenRepository = refreshTokenRepository,
            passwordEncoder = passwordEncoder,
            jwtTokenService = jwtTokenService,
            tokenBlacklistService = tokenBlacklistService,
            accessTokenExp = 900000L,
            refreshTokenExp = 604800000L
        )
    }

    @Test
    fun `register creates user and returns tokens`() {
        val userEntity = UserEntity(
            id = UUID.randomUUID(),
            email = "test@example.com",
            passwordHash = BCryptPasswordEncoder().encode("password123")!!,
            roles = "USER",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        wheneverBlocking { userRepository.existsByEmail(any()) } doReturn false
        wheneverBlocking { userRepository.save(any<UserEntity>()) } doReturn userEntity
        wheneverBlocking { refreshTokenRepository.save(any<RefreshTokenEntity>()) } doReturn mock()

        val result = runBlocking {
            authService.register("test@example.com", "password123")
        }

        assertNotNull(result.accessToken)
        assertNotNull(result.refreshToken)
        org.junit.jupiter.api.Assertions.assertTrue(result.expiresIn > 0)
    }

    @Test
    fun `register throws when email exists`() {
        wheneverBlocking { userRepository.existsByEmail(any()) } doReturn true

        assertThrows(EmailAlreadyExistsException::class.java) {
            runBlocking {
                authService.register("existing@example.com", "password123")
            }
        }
    }

    @Test
    fun `login returns tokens for valid credentials`() {
        val userId = UUID.randomUUID()
        val passwordHash = BCryptPasswordEncoder().encode("password123")!!
        val userEntity = UserEntity(
            id = userId,
            email = "test@example.com",
            passwordHash = passwordHash,
            roles = "USER",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        wheneverBlocking { userRepository.findByEmail(any()) } doReturn userEntity
        wheneverBlocking { refreshTokenRepository.save(any<RefreshTokenEntity>()) } doReturn mock()

        val result = runBlocking {
            authService.login("test@example.com", "password123")
        }

        assertNotNull(result.accessToken)
        assertNotNull(result.refreshToken)
    }

    @Test
    fun `login throws for invalid password`() {
        val userId = UUID.randomUUID()
        val passwordHash = BCryptPasswordEncoder().encode("correctpassword")!!
        val userEntity = UserEntity(
            id = userId,
            email = "test@example.com",
            passwordHash = passwordHash,
            roles = "USER",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        wheneverBlocking { userRepository.findByEmail(any()) } doReturn userEntity

        assertThrows(InvalidCredentialsException::class.java) {
            runBlocking {
                authService.login("test@example.com", "wrongpassword")
            }
        }
    }

    @Test
    fun `refresh throws for expired token`() {
        val userId = UUID.randomUUID()
        val expiredToken = RefreshTokenEntity(
            id = UUID.randomUUID(),
            userId = userId,
            token = "expired-token",
            expiresAt = Instant.now().minusSeconds(1),
            revoked = false,
            createdAt = Instant.now().minusSeconds(86400)
        )

        wheneverBlocking { refreshTokenRepository.findByToken(any()) } doReturn expiredToken

        assertThrows(InvalidRefreshTokenException::class.java) {
            runBlocking {
                authService.refresh("expired-token")
            }
        }
    }
}
