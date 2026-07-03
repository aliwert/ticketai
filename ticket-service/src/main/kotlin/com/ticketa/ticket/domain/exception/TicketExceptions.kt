package com.ticketa.ticket.domain.exception

import java.util.UUID

sealed class TicketException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class TicketNotFoundException(id: UUID) : TicketException("Ticket not found: $id")

class InvalidTicketStateException(message: String) : TicketException(message)

class DuplicateEventException(eventId: String) : TicketException("Duplicate event: $eventId")

class TicketAlreadyUsedException(ticketId: UUID) : TicketException("Ticket already used: $ticketId")
