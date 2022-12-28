package com.app.statuscontrol.domain.interactor.notification

import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Notification
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseSendNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(laneStatus: LaneStatus) : Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val notification = Notification(
            lane = laneStatus.lane,
            lastModification = laneStatus.lastModification,
            status = laneStatus.status
        )
        if (notification.lane.isNotEmpty()) {
            notificationRepository.sendNotification(notification)
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error("Notification error"))
        }
    }
}