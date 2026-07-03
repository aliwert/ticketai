package com.ticketa.notification.application.service

import com.ticketa.notification.domain.model.NotificationChannel
import com.ticketa.notification.domain.model.NotificationFailureReason

data class SendResult(
    val success: Boolean,
    val failureReason: NotificationFailureReason? = null
)

interface NotificationSender {
    val channel: NotificationChannel
    suspend fun send(recipient: String, subject: String, body: String): SendResult
}
