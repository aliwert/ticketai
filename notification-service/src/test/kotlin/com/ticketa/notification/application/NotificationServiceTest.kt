package com.ticketa.notification.application

import com.ticketa.notification.application.service.EmailNotificationSender
import com.ticketa.notification.application.service.NotificationService
import com.ticketa.notification.application.service.SendResult
import com.ticketa.notification.application.service.SmsNotificationSender
import com.ticketa.notification.application.template.TicketIssuedEmailTemplate
import com.ticketa.notification.application.template.TicketIssuedSmsTemplate
import com.ticketa.notification.domain.event.TicketIssuedEvent
import com.ticketa.notification.domain.exception.DuplicateEventException
import com.ticketa.notification.domain.exception.NotificationNotFoundException
import com.ticketa.notification.domain.model.NotificationStatus
import com.ticketa.notification.infrastructure.persistence.NotificationEntity
import com.ticketa.notification.infrastructure.persistence.NotificationRepository
import com.ticketa.notification.infrastructure.persistence.ProcessedEventRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking
import java.time.Instant
import java.util.UUID

class NotificationServiceTest {

    private val notificationRepository: NotificationRepository = mock()
    private val processedEventRepository: ProcessedEventRepository = mock()
    private val emailSender: EmailNotificationSender = mock()
    private val smsSender: SmsNotificationSender = mock()
    private val emailTemplate = TicketIssuedEmailTemplate()
    private val smsTemplate = TicketIssuedSmsTemplate()
    private val notificationService = NotificationService(
        notificationRepository = notificationRepository,
        processedEventRepository = processedEventRepository,
        emailSender = emailSender,
        smsSender = smsSender,
        ticketIssuedEmailTemplate = emailTemplate,
        ticketIssuedSmsTemplate = smsTemplate
    )

    @Test
    fun `should get notification by id`() = runBlocking {
        val notificationId = UUID.randomUUID()
        val entity = NotificationEntity(
            id = notificationId,
            userId = "user-1",
            channel = "EMAIL",
            type = "TICKET_ISSUED",
            status = "SENT",
            subject = "Subject",
            body = "Body",
            recipient = "user-1@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { notificationRepository.findById(notificationId) } doReturn entity

        val notification = notificationService.getNotification(notificationId)
        assertEquals(notificationId, notification.id)
        assertEquals(NotificationStatus.SENT, notification.status)
    }

    @Test
    fun `should throw when notification not found`() {
        val notificationId = UUID.randomUUID()
        wheneverBlocking { notificationRepository.findById(notificationId) } doReturn null

        assertThrows(NotificationNotFoundException::class.java) {
            runBlocking { notificationService.getNotification(notificationId) }
        }
    }

    @Test
    fun `should get user notifications`() = runBlocking {
        val userId = "user-1"
        val entity = NotificationEntity(
            id = UUID.randomUUID(),
            userId = userId,
            channel = "EMAIL",
            type = "TICKET_ISSUED",
            status = "SENT",
            subject = "Subject",
            body = "Body",
            recipient = "$userId@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { notificationRepository.findByUserId(userId) } doReturn listOf(entity)

        val notifications = notificationService.getUserNotifications(userId)
        assertEquals(1, notifications.size)
        assertEquals(userId, notifications[0].userId)
    }

    @Test
    fun `should reject duplicate ticket events`() {
        val event = TicketIssuedEvent(
            eventId = UUID.randomUUID().toString(),
            ticketId = UUID.randomUUID(),
            reservationId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            userId = "user-1",
            sessionId = UUID.randomUUID(),
            seatId = UUID.randomUUID()
        )

        wheneverBlocking { processedEventRepository.existsById(event.eventId) } doReturn true

        assertThrows(DuplicateEventException::class.java) {
            runBlocking { notificationService.handleTicketIssuedEvent(event) }
        }
    }

    @Test
    fun `should get notifications by status`() = runBlocking {
        val entity = NotificationEntity(
            id = UUID.randomUUID(),
            userId = "user-1",
            channel = "EMAIL",
            type = "TICKET_ISSUED",
            status = "PENDING",
            subject = "Subject",
            body = "Body",
            recipient = "user-1@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        wheneverBlocking { notificationRepository.findByStatus("PENDING") } doReturn listOf(entity)

        val notifications = notificationService.getNotificationsByStatus(NotificationStatus.PENDING)
        assertEquals(1, notifications.size)
        assertEquals(NotificationStatus.PENDING, notifications[0].status)
    }
}
