package com.ticketa.auth.infrastructure.persistence

import com.ticketa.auth.domain.model.Role
import com.ticketa.auth.domain.model.User
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("users")
data class UserEntity(
    @Id val id: UUID,
    val email: String,
    @Column("password_hash") val passwordHash: String,
    val roles: String,
    @Column("created_at") val createdAt: Instant,
    @Column("updated_at") val updatedAt: Instant
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        passwordHash = passwordHash,
        roles = roles.split(",").map { Role.valueOf(it.trim()) }.toSet(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(
            id = user.id,
            email = user.email,
            passwordHash = user.passwordHash,
            roles = user.roles.joinToString(",") { it.name },
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}
