package com.app.statuscontrol.data.remote.user

import com.app.statuscontrol.data.util.FirebaseConstants
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.repository.UserStatusRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserStatusRepositoryImpl @Inject constructor(

): UserStatusRepository {

    override suspend fun getAllUserStatus(): Resource<List<User>> {
        return try {
            val notesList = FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS_COLLECTION)
                .get()
                .await()
                .toObjects(User::class.java)

            Resource.Success(notesList)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }
}