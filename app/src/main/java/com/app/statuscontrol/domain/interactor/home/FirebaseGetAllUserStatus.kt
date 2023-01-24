package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.data.util.FirebaseConstants
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.repository.UserStatusRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseGetAllUserStatus @Inject constructor(
    private val userStatusRepository: UserStatusRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<User>>> = callbackFlow {
        val event = FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS_COLLECTION)

        val subscription = event.addSnapshotListener { snapshot, error ->

            if (error != null) {
                this.trySend(Resource.Error(error.message.toString())).isSuccess
                return@addSnapshotListener
            }

            if (snapshot != null) {

                val userList = arrayListOf<User>()
                snapshot.documents.map {
                    val laneStatus = it.toObject(User::class.java)
                    laneStatus?.let { lane -> userList.add(lane) }
                }

                this.trySend(Resource.Success(userList)).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }
}