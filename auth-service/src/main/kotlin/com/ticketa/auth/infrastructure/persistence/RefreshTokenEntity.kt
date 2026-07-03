package com.ticketa.auth.infrastructure.persistence

import com.ticketa.auth.domain.model.RefreshToken
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("refresh_tokens")
data class RefreshTokenEntity(
    @Id val id: UUID,
    @Column("user_id") val userId: UUID,
    val token: String,
    @Column("expires_at") val expiresAt: Instant,
    val revoked: Boolean,
    @Column("created_at") val createdAt: Instant
) {
    fun toDomain(): RefreshToken = RefreshToken(
        id = id,
        userId = userId,
        token = token,
        expiresAt = expiresAt,
        revoked = revoked,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(refreshToken: RefreshToken): RefreshTokenEntity = RefreshTokenEntity(
            id = refreshToken.id,
            userId = refreshToken.userId,
            token = refreshToken.token,
            expiresAt = refreshToken.expiresAt,
            revoked = refreshToken.revoked,
            createdAt = refreshToken.createdAt
        )
    }
}
