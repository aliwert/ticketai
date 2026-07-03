package com.ticketa.notification.application.service

import com.ticketa.notification.application.template.NotificationContent
import com.ticketa.notification.application.template.NotificationTemplate
import com.ticketa.notification.application.template.TicketIssuedEmailTemplate
import com.ticketa.notification.application.template.TicketIssuedSmsTemplate
import com.ticketa.notification.domain.event.TicketIssuedEvent
import com.ticketa.notification.domain.exception.DuplicateEventException
import com.ticketa.notification.domain.exception.NotificationNotFoundException
import com.ticketa.notification.domain.exception.UnsupportedChannelException
import com.ticketa.notification.domain.model.Notification
import com.ticketa.notification.domain.model.NotificationChannel
import com.ticketa.notification.domain.model.NotificationStatus
import com.ticketa.notification.domain.model.NotificationType
import com.ticketa.notification.infrastructure.persistence.NotificationEntity
import com.ticketa.notification.infrastructure.persistence.NotificationRepository
import com.ticketa.notification.infrastructure.persistence.ProcessedEventEntity
import com.ticketa.notification.infrastructure.persistence.ProcessedEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val processedEventRepository: ProcessedEventRepository,
    private val emailSender: EmailNotificationSender,
    private val smsSender: SmsNotificationSender,
    private val ticketIssuedEmailTemplate: TicketIssuedEmailTemplate,
    private val ticketIssuedSmsTemplate: TicketIssuedSmsTemplate
) {
    private val log = LoggerFactory.getLogger(NotificationService::class.java)

    private val senders: Map<NotificationChannel, NotificationSender> = mapOf(
        NotificationChannel.EMAIL to emailSender,
        NotificationChannel.SMS to smsSender
    )

    private val templates: Map<NotificationType, Map<NotificationChannel, NotificationTemplate>> = mapOf(
        NotificationType.TICKET_ISSUED to mapOf(
            NotificationChannel.EMAIL to ticketIssuedEmailTemplate,
            NotificationChannel.SMS to ticketIssuedSmsTemplate
        )
    )

    @Transactional
    suspend fun handleTicketIssuedEvent(event: TicketIssuedEvent) {
        if (processedEventRepository.existsById(event.eventId)) {
            log.debug("Duplicate event ignored: {}", event.eventId)
            throw DuplicateEventException(event.eventId)
        }

        processedEventRepository.save(
            ProcessedEventEntity(eventId = event.eventId, eventType = "TicketIssuedEvent")
        )

        sendNotification(
            userId = event.userId,
            ticketId = event.ticketId,
            event.sessionId,
            event.seatId,
            type = NotificationType.TICKET_ISSUED
        )
    }

    @Transactional
    suspend fun sendNotification(
        userId: String,
        ticketId: UUID,
        sessionId: UUID,
        seatId: UUID,
        type: NotificationType
    ) {
        for ((channel, sender) in senders) {
            val template = resolveTemplate(type, channel)
            val content = template.render(
                ticketId = ticketId.toString(),
                userId = userId,
                sessionId = sessionId.toString(),
                seatId = seatId.toString()
            )
            val recipient = resolveRecipient(userId, channel)
            val notificationId = UUID.randomUUID()

            val notification = Notification(
                id = notificationId,
                userId = userId,
                ticketId = ticketId,
                channel = channel,
                type = type,
                status = NotificationStatus.PENDING,
                subject = content.subject,
                body = content.body,
                recipient = recipient,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            notificationRepository.save(NotificationEntity.fromDomain(notification))

            val result = sender.send(recipient, content.subject, content.body)
            if (result.success) {
                val sent = notification.markSent()
                notificationRepository.save(NotificationEntity.fromDomain(sent))
                log.info("Notification sent: id={}, channel={}", notificationId, channel)
            } else {
                val failed = notification.markFailed(result.failureReason!!)
                notificationRepository.save(NotificationEntity.fromDomain(failed))
                log.warn("Notification failed: id={}, channel={}, reason={}", notificationId, channel, result.failureReason)
            }
        }
    }

    private fun resolveTemplate(type: NotificationType, channel: NotificationChannel): NotificationTemplate {
        return templates[type]?.get(channel)
            ?: throw UnsupportedChannelException("$type / $channel")
    }

    private fun resolveRecipient(userId: String, channel: NotificationChannel): String {
        return when (channel) {
            NotificationChannel.EMAIL -> "$userId@example.com"
            NotificationChannel.SMS -> "+1555$userId"
        }
    }

    suspend fun getNotification(notificationId: UUID): Notification {
        val entity = notificationRepository.findById(notificationId)
            ?: throw NotificationNotFoundException(notificationId)
        return entity.toDomain()
    }

    suspend fun getUserNotifications(userId: String): List<Notification> {
        return notificationRepository.findByUserId(userId).map { it.toDomain() }
    }

    suspend fun getNotificationsByStatus(status: NotificationStatus): List<Notification> {
        return notificationRepository.findByStatus(status.name).map { it.toDomain() }
    }
}
