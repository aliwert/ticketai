package com.ticketa.notification.application.service

import com.ticketa.notification.domain.model.NotificationChannel
import com.ticketa.notification.domain.model.NotificationFailureReason
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailNotificationSender(
    private val mailSender: JavaMailSender
) : NotificationSender {

    private val log = LoggerFactory.getLogger(EmailNotificationSender::class.java)

    override val channel: NotificationChannel = NotificationChannel.EMAIL

    override suspend fun send(recipient: String, subject: String, body: String): SendResult {
        return try {
            val message = SimpleMailMessage().apply {
                setTo(recipient)
                setSubject(subject)
                setText(body)
            }
            mailSender.send(message)
            log.info("Email sent to: {}", recipient)
            SendResult(success = true)
        } catch (e: Exception) {
            log.error("Failed to send email to: {}", recipient, e)
            SendResult(success = false, failureReason = NotificationFailureReason.SMTP_ERROR)
        }
    }
}
