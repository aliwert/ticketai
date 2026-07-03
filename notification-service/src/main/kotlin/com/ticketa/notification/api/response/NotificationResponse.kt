package com.ticketa.notification.api.response

import com.ticketa.notification.domain.model.Notification
import com.ticketa.notification.domain.model.NotificationChannel
import com.ticketa.notification.domain.model.NotificationFailureReason
import com.ticketa.notification.domain.model.NotificationStatus
import com.ticketa.notification.domain.model.NotificationType
import java.time.Instant
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val userId: String,
    val ticketId: UUID?,
    val channel: NotificationChannel,
    val type: NotificationType,
    val status: NotificationStatus,
    val subject: String,
    val recipient: String,
    val failureReason: NotificationFailureReason?,
    val sentAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(notification: Notification): NotificationResponse = NotificationResponse(
            id = notification.id,
            userId = notification.userId,
            ticketId = notification.ticketId,
            channel = notification.channel,
            type = notification.type,
            status = notification.status,
            subject = notification.subject,
            recipient = notification.recipient,
            failureReason = notification.failureReason,
            sentAt = notification.sentAt,
            createdAt = notification.createdAt,
            updatedAt = notification.updatedAt
        )
    }
}
