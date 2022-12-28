package com.app.statuscontrol.data.remote.notification

import com.app.statuscontrol.data.util.FirebaseConstants
import com.app.statuscontrol.domain.model.Notification
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.repository.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseNotificationRepositoryImp @Inject constructor(

): NotificationRepository {

    override suspend fun sendNotification(notification: Notification): Boolean {
        return try {
            var isSuccessful = false
            var _notification: Notification? = Notification()

            FirebaseFirestore.getInstance().collection(FirebaseConstants.NOTIFICATION_COLLECTION)
                .orderBy("lastModification", Query.Direction.DESCENDING)
                .limit(1)
                .get().addOnSuccessListener {
                    it.documents.map { item ->
                        _notification = item.toObject(Notification::class.java)
                        _notification?.let { _not ->
                            _not.id = item.id
                        }
                     }
                }

                .addOnFailureListener {
                    _notification = null
                    it.printStackTrace()
                }
                .await()

            val id = if (_notification != null) {
                _notification!!.id.toInt() + 1
            } else {
                notification.id.toInt()
            }

            FirebaseFirestore.getInstance().collection(FirebaseConstants.NOTIFICATION_COLLECTION)
                .document(id.toString())
                .set(notification, SetOptions.merge())
                .addOnCompleteListener {
                    isSuccessful = it.isSuccessful
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .await()
            isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getLastNotification(): Resource<List<Notification>> {
        TODO("Not yet implemented")
    }
}