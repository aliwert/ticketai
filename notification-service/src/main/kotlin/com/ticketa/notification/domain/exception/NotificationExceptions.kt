package com.ticketa.notification.domain.exception

import java.util.UUID

sealed class NotificationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class NotificationNotFoundException(id: UUID) : NotificationException("Notification not found: $id")

class InvalidNotificationStateException(message: String) : NotificationException(message)

class DuplicateEventException(eventId: String) : NotificationException("Duplicate event: $eventId")

class UnsupportedChannelException(channel: String) : NotificationException("Unsupported channel: $channel")
