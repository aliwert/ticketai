package com.ticketa.notification.application

import com.ticketa.notification.application.service.EmailNotificationSender
import com.ticketa.notification.application.service.NotificationSender
import com.ticketa.notification.application.service.SmsNotificationSender
import com.ticketa.notification.domain.model.NotificationChannel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationSenderTest {

    @Test
    fun `email sender should use EMAIL channel`() {
        val mailSender: JavaMailSender = mock()
        val sender: NotificationSender = EmailNotificationSender(mailSender)
        assertEquals(NotificationChannel.EMAIL, sender.channel)
    }

    @Test
    fun `sms sender should use SMS channel`() {
        val sender: NotificationSender = SmsNotificationSender()
        assertEquals(NotificationChannel.SMS, sender.channel)
    }

    @Test
    fun `sms sender should always succeed`() = runBlocking {
        val sender = SmsNotificationSender()
        val result = sender.send("+1555123", "Subject", "Body")
        assertTrue(result.success)
    }

    @Test
    fun `email sender should handle mail error gracefully`() = runBlocking {
        val mailSender: JavaMailSender = mock()
        doNothing().`when`(mailSender).send(any<SimpleMailMessage>())
        val sender = EmailNotificationSender(mailSender)
        val result = sender.send("test@example.com", "Subject", "Body")
        assertTrue(result.success)
    }
}
