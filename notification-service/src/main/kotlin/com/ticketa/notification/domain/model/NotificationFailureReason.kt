package com.ticketa.notification.domain.model

enum class NotificationFailureReason {
    INVALID_EMAIL,
    INVALID_PHONE,
    SMTP_ERROR,
    TEMPLATE_ERROR,
    UNKNOWN_CHANNEL,
    UNKNOWN
}
