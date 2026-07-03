package com.ticketa.notification.application

import com.ticketa.notification.application.template.TicketIssuedEmailTemplate
import com.ticketa.notification.application.template.TicketIssuedSmsTemplate
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertTrue

class TemplateTest {

    private val emailTemplate = TicketIssuedEmailTemplate()
    private val smsTemplate = TicketIssuedSmsTemplate()

    @Test
    fun `email template should generate subject and body`() {
        val content = emailTemplate.render(
            ticketId = UUID.randomUUID().toString(),
            userId = "user-1",
            sessionId = UUID.randomUUID().toString(),
            seatId = UUID.randomUUID().toString()
        )
        assertTrue(content.subject.isNotBlank())
        assertTrue(content.body.isNotBlank())
        assertTrue(content.subject.contains("Ticket"))
    }

    @Test
    fun `sms template should generate subject and body`() {
        val content = smsTemplate.render(
            ticketId = UUID.randomUUID().toString(),
            userId = "user-1",
            sessionId = UUID.randomUUID().toString(),
            seatId = UUID.randomUUID().toString()
        )
        assertTrue(content.subject.isNotBlank())
        assertTrue(content.body.isNotBlank())
        assertTrue(content.body.contains("ticket"))
    }
}
