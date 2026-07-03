package com.ticketa.notification.application.template

import org.springframework.stereotype.Component

@Component
class TicketIssuedEmailTemplate : NotificationTemplate {

    override fun render(ticketId: String, userId: String, sessionId: String, seatId: String): NotificationContent {
        val subject = "Your Ticket Has Been Issued"
        val body = buildString {
            appendLine("Hello,")
            appendLine()
            appendLine("Your ticket has been successfully issued.")
            appendLine()
            appendLine("Ticket Details:")
            appendLine("  Ticket ID: $ticketId")
            appendLine("  Session: $sessionId")
            appendLine("  Seat: $seatId")
            appendLine()
            appendLine("Thank you for your purchase!")
            appendLine("Ticketa-AI Team")
        }
        return NotificationContent(subject = subject, body = body)
    }
}
