package com.app.statuscontrol.domain.repository

import com.app.statuscontrol.domain.model.Notification
import com.app.statuscontrol.domain.model.Resource

interface NotificationRepository {
    suspend fun sendNotification(notification: Notification): Boolean
    suspend fun getLastNotification(): Resource<List<Notification>>
}