package com.ticketa.notification.controller

import com.ticketa.notification.api.response.NotificationResponse
import com.ticketa.notification.application.service.NotificationService
import com.ticketa.notification.domain.model.NotificationStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {
    @GetMapping("/{notificationId}")
    suspend fun getNotification(@PathVariable notificationId: UUID): ResponseEntity<NotificationResponse> {
        val notification = notificationService.getNotification(notificationId)
        return ResponseEntity.ok(NotificationResponse.from(notification))
    }

    @GetMapping("/user/{userId}")
    suspend fun getUserNotifications(@PathVariable userId: String): ResponseEntity<List<NotificationResponse>> {
        val notifications = notificationService.getUserNotifications(userId)
        return ResponseEntity.ok(notifications.map { NotificationResponse.from(it) })
    }

    @GetMapping
    suspend fun getNotificationsByStatus(@RequestParam status: String): ResponseEntity<List<NotificationResponse>> {
        val notificationStatus = NotificationStatus.valueOf(status.uppercase())
        val notifications = notificationService.getNotificationsByStatus(notificationStatus)
        return ResponseEntity.ok(notifications.map { NotificationResponse.from(it) })
    }
}
