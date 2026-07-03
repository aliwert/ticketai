package com.ticketa.notification.application.template

import org.springframework.stereotype.Component

@Component
class TicketIssuedSmsTemplate : NotificationTemplate {

    override fun render(ticketId: String, userId: String, sessionId: String, seatId: String): NotificationContent {
        val subject = "Ticket Issued"
        val body = "Your ticket ($ticketId) for session $sessionId, seat $seatId has been issued."
        return NotificationContent(subject = subject, body = body)
    }
}
