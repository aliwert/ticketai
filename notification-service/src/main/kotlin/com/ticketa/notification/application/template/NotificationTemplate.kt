package com.ticketa.notification.application.template

data class NotificationContent(
    val subject: String,
    val body: String
)

interface NotificationTemplate {
    fun render(ticketId: String, userId: String, sessionId: String, seatId: String): NotificationContent
}
