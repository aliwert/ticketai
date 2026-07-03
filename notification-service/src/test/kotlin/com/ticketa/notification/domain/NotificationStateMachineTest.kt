package com.ticketa.notification.domain

import com.ticketa.notification.domain.model.Notification
import com.ticketa.notification.domain.model.NotificationChannel
import com.ticketa.notification.domain.model.NotificationFailureReason
import com.ticketa.notification.domain.model.NotificationStatus
import com.ticketa.notification.domain.model.NotificationType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class NotificationStateMachineTest {

    private fun createNotification(status: NotificationStatus = NotificationStatus.PENDING): Notification = Notification(
        id = UUID.randomUUID(),
        userId = "user-1",
        channel = NotificationChannel.EMAIL,
        type = NotificationType.TICKET_ISSUED,
        status = status,
        subject = "Subject",
        body = "Body",
        recipient = "user-1@example.com",
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    @Test
    fun `should start in PENDING state`() {
        val notification = createNotification()
        assertEquals(NotificationStatus.PENDING, notification.status)
    }

    @Test
    fun `should transition from PENDING to SENT`() {
        val notification = createNotification()
        val sent = notification.markSent()
        assertEquals(NotificationStatus.SENT, sent.status)
        assertNotNull(sent.sentAt)
    }

    @Test
    fun `should transition from PENDING to FAILED`() {
        val notification = createNotification()
        val failed = notification.markFailed(NotificationFailureReason.SMTP_ERROR)
        assertEquals(NotificationStatus.FAILED, failed.status)
        assertEquals(NotificationFailureReason.SMTP_ERROR, failed.failureReason)
    }

    @Test
    fun `should reject markSent from SENT state`() {
        val notification = createNotification(NotificationStatus.SENT)
        assertThrows(IllegalArgumentException::class.java) { notification.markSent() }
    }

    @Test
    fun `should reject markFailed from SENT state`() {
        val notification = createNotification(NotificationStatus.SENT)
        assertThrows(IllegalArgumentException::class.java) { notification.markFailed(NotificationFailureReason.UNKNOWN) }
    }
}
