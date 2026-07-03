package com.ticketa.notification.application.service

import com.ticketa.notification.domain.model.NotificationChannel
import com.ticketa.notification.domain.model.NotificationFailureReason
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SmsNotificationSender : NotificationSender {

    private val log = LoggerFactory.getLogger(SmsNotificationSender::class.java)

    override val channel: NotificationChannel = NotificationChannel.SMS

    override suspend fun send(recipient: String, subject: String, body: String): SendResult {
        log.info("Mock SMS sent to: {}, body: {}", recipient, body)
        return SendResult(success = true)
    }
}
