package com.ticketa.auth.application.service

import com.ticketa.auth.domain.exception.EmailAlreadyExistsException
import com.ticketa.auth.domain.exception.InvalidCredentialsException
import com.ticketa.auth.domain.exception.InvalidRefreshTokenException
import com.ticketa.auth.domain.model.RefreshToken
import com.ticketa.auth.domain.model.Role
import com.ticketa.auth.domain.model.User
import com.ticketa.auth.infrastructure.persistence.RefreshTokenEntity
import com.ticketa.auth.infrastructure.persistence.RefreshTokenRepository
import com.ticketa.auth.infrastructure.persistence.UserEntity
import com.ticketa.auth.infrastructure.persistence.UserRepository
import com.ticketa.auth.infrastructure.redis.TokenBlacklistService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenService: JwtTokenService,
    private val tokenBlacklistService: TokenBlacklistService,
    @Value("\${app.jwt.access-token-expiration}") private val accessTokenExp: Long,
    @Value("\${app.jwt.refresh-token-expiration}") private val refreshTokenExp: Long
) {
    @Transactional
    suspend fun register(email: String, password: String): AuthResult {
        if (userRepository.existsByEmail(email)) {
            throw EmailAlreadyExistsException(email)
        }

        val now = Instant.now()
        val passwordHash: String = passwordEncoder.encode(password).toString()
        val user = User(
            id = UUID.randomUUID(),
            email = email,
            passwordHash = passwordHash,
            roles = setOf(Role.USER),
            createdAt = now,
            updatedAt = now
        )

        userRepository.save(UserEntity.fromDomain(user))

        val refreshToken = createRefreshToken(user.id)
        val accessToken = jwtTokenService.generateAccessToken(
            userId = user.id,
            email = user.email,
            roles = user.roles.map { it.name }.toSet()
        )

        return AuthResult(
            accessToken = accessToken,
            refreshToken = refreshToken.token,
            expiresIn = accessTokenExp
        )
    }

    @Transactional
    suspend fun login(email: String, password: String): AuthResult {
        val userEntity = userRepository.findByEmail(email)
            ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(password, userEntity.passwordHash)) {
            throw InvalidCredentialsException()
        }

        val user = userEntity.toDomain()
        val refreshToken = createRefreshToken(user.id)
        val accessToken = jwtTokenService.generateAccessToken(
            userId = user.id,
            email = user.email,
            roles = user.roles.map { it.name }.toSet()
        )

        return AuthResult(
            accessToken = accessToken,
            refreshToken = refreshToken.token,
            expiresIn = accessTokenExp
        )
    }

    @Transactional
    suspend fun refresh(refreshTokenValue: String): AuthResult {
        val tokenEntity = refreshTokenRepository.findByToken(refreshTokenValue)
            ?: throw InvalidRefreshTokenException()

        val refreshToken = tokenEntity.toDomain()

        if (refreshToken.revoked || refreshToken.expiresAt.isBefore(Instant.now())) {
            throw InvalidRefreshTokenException()
        }

        refreshTokenRepository.save(tokenEntity.copy(revoked = true))

        val userEntity = userRepository.findById(refreshToken.userId)
            ?: throw InvalidRefreshTokenException()

        val user = userEntity.toDomain()
        val newRefreshToken = createRefreshToken(user.id)
        val accessToken = jwtTokenService.generateAccessToken(
            userId = user.id,
            email = user.email,
            roles = user.roles.map { it.name }.toSet()
        )

        return AuthResult(
            accessToken = accessToken,
            refreshToken = newRefreshToken.token,
            expiresIn = accessTokenExp
        )
    }

    @Transactional
    suspend fun logout(accessToken: String, refreshTokenValue: String?) {
        val jti = jwtTokenService.extractJti(accessToken)
        if (jti != null) {
            val ttl = jwtTokenService.extractExpiration(accessToken)
            if (ttl != null) {
                tokenBlacklistService.blacklist(jti, ttl - System.currentTimeMillis())
            }
        }

        if (refreshTokenValue != null) {
            val tokenEntity = refreshTokenRepository.findByToken(refreshTokenValue)
            if (tokenEntity != null) {
                refreshTokenRepository.save(tokenEntity.copy(revoked = true))
            }
        }
    }

    private suspend fun createRefreshToken(userId: UUID): RefreshToken {
        val now = Instant.now()
        val refreshToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = userId,
            token = UUID.randomUUID().toString(),
            expiresAt = now.plusMillis(refreshTokenExp),
            revoked = false,
            createdAt = now
        )
        refreshTokenRepository.save(RefreshTokenEntity.fromDomain(refreshToken))
        return refreshToken
    }
}

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)
