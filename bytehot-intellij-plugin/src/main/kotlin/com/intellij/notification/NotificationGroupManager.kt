package com.intellij.notification

import com.intellij.openapi.project.Project

/**
 * Stub implementation of IntelliJ NotificationGroupManager for compilation.
 * In real implementation, this would be provided by IntelliJ Platform SDK.
 */
class NotificationGroupManager {
    companion object {
        fun getInstance(): NotificationGroupManager = NotificationGroupManager()
    }
    
    fun getNotificationGroup(groupId: String): NotificationGroup {
        return NotificationGroup()
    }
}

class NotificationGroup {
    fun createNotification(title: String, content: String, type: NotificationType): Notification {
        return Notification(title, content, type)
    }
}

class Notification(val title: String, val content: String, val type: NotificationType) {
    fun notify(project: Project?) {
        println("[$type] $title: $content")
    }
}

enum class NotificationType {
    INFORMATION, ERROR, WARNING
}