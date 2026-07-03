package com.ticketa.notification.controller

import com.ticketa.notification.application.service.NotificationService
import com.ticketa.notification.domain.model.Notification
import com.ticketa.notification.domain.model.NotificationChannel
import com.ticketa.notification.domain.model.NotificationStatus
import com.ticketa.notification.domain.model.NotificationType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import java.time.Instant
import java.util.UUID

class NotificationControllerTest {

    private val notificationService: NotificationService = mock()
    private val controller = NotificationController(notificationService)

    @Test
    fun `should return notification by id`() = runBlocking {
        val notificationId = UUID.randomUUID()
        val notification = Notification(
            id = notificationId,
            userId = "user-1",
            channel = NotificationChannel.EMAIL,
            type = NotificationType.TICKET_ISSUED,
            status = NotificationStatus.SENT,
            subject = "Subject",
            body = "Body",
            recipient = "user-1@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { notificationService.getNotification(notificationId) } doReturn notification

        val response = controller.getNotification(notificationId)
        assertEquals(200, response.statusCode.value())
        assertNotNull(response.body)
        assertEquals(notificationId, response.body!!.id)
    }

    @Test
    fun `should return user notifications`() = runBlocking {
        val userId = "user-1"
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = userId,
            channel = NotificationChannel.EMAIL,
            type = NotificationType.TICKET_ISSUED,
            status = NotificationStatus.SENT,
            subject = "Subject",
            body = "Body",
            recipient = "$userId@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { notificationService.getUserNotifications(userId) } doReturn listOf(notification)

        val response = controller.getUserNotifications(userId)
        assertEquals(200, response.statusCode.value())
        assertEquals(1, response.body!!.size)
        assertEquals(userId, response.body!![0].userId)
    }

    @Test
    fun `should return notifications by status`() = runBlocking {
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = "user-1",
            channel = NotificationChannel.EMAIL,
            type = NotificationType.TICKET_ISSUED,
            status = NotificationStatus.PENDING,
            subject = "Subject",
            body = "Body",
            recipient = "user-1@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { notificationService.getNotificationsByStatus(NotificationStatus.PENDING) } doReturn listOf(notification)

        val response = controller.getNotificationsByStatus("PENDING")
        assertEquals(200, response.statusCode.value())
        assertEquals(1, response.body!!.size)
        assertEquals(NotificationStatus.PENDING, response.body!![0].status)
    }
}
