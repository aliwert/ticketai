package com.ticketa.notification.infrastructure.persistence

import com.ticketa.notification.domain.model.Notification
import com.ticketa.notification.domain.model.NotificationChannel
import com.ticketa.notification.domain.model.NotificationFailureReason
import com.ticketa.notification.domain.model.NotificationStatus
import com.ticketa.notification.domain.model.NotificationType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("notifications")
data class NotificationEntity(
    @Id
    val id: UUID,
    val userId: String,
    val ticketId: UUID? = null,
    val channel: String,
    val type: String,
    val status: String,
    val subject: String,
    val body: String,
    val recipient: String,
    val failureReason: String? = null,
    val sentAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun toDomain(): Notification = Notification(
        id = id,
        userId = userId,
        ticketId = ticketId,
        channel = NotificationChannel.valueOf(channel),
        type = NotificationType.valueOf(type),
        status = NotificationStatus.valueOf(status),
        subject = subject,
        body = body,
        recipient = recipient,
        failureReason = failureReason?.let { NotificationFailureReason.valueOf(it) },
        sentAt = sentAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(notification: Notification): NotificationEntity = NotificationEntity(
            id = notification.id,
            userId = notification.userId,
            ticketId = notification.ticketId,
            channel = notification.channel.name,
            type = notification.type.name,
            status = notification.status.name,
            subject = notification.subject,
            body = notification.body,
            recipient = notification.recipient,
            failureReason = notification.failureReason?.name,
            sentAt = notification.sentAt,
            createdAt = notification.createdAt,
            updatedAt = notification.updatedAt
        )
    }
}
