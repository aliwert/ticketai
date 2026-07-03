package com.ticketa.notification.domain.model

import java.time.Instant
import java.util.UUID

data class Notification(
    val id: UUID,
    val userId: String,
    val ticketId: UUID? = null,
    val channel: NotificationChannel,
    val type: NotificationType,
    val status: NotificationStatus,
    val subject: String,
    val body: String,
    val recipient: String,
    val failureReason: NotificationFailureReason? = null,
    val sentAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun markSent(): Notification {
        require(status == NotificationStatus.PENDING) { "Can only mark PENDING as sent, current: $status" }
        return copy(
            status = NotificationStatus.SENT,
            sentAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    fun markFailed(reason: NotificationFailureReason): Notification {
        require(status == NotificationStatus.PENDING) { "Can only mark PENDING as failed, current: $status" }
        return copy(
            status = NotificationStatus.FAILED,
            failureReason = reason,
            updatedAt = Instant.now()
        )
    }
}
