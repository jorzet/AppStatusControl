package com.app.statuscontrol.domain.interactor.notification

import com.app.statuscontrol.data.util.FirebaseConstants
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Notification
import com.app.statuscontrol.domain.model.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseGetLastNotificationUseCase @Inject constructor(

) {
    suspend operator fun invoke(): Flow<Resource<Notification>> = callbackFlow {
        val event: Query = FirebaseFirestore.getInstance().collection(FirebaseConstants.NOTIFICATION_COLLECTION)
            .orderBy("lastModification", Query.Direction.DESCENDING)
            .limit(1)

        val subscription = event.addSnapshotListener { snapshot, error ->

            if (error != null) {
                this.trySend(Resource.Error(error.message.toString())).isSuccess
                return@addSnapshotListener
            }

            if (snapshot != null) {

                var notificationStatus = Notification()
                snapshot.documents.map {
                    val notification = it.toObject(Notification::class.java)
                    notification?.let { not ->
                        notificationStatus = not
                        notification.id = it.id
                    }
                }

                this.trySend(Resource.Success(notificationStatus)).isSuccess
            }
        }
        awaitClose {
            subscription.remove()
        }
    }
}