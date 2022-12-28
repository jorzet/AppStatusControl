package com.app.statuscontrol.data.remote.user

import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.repository.UserRepository
import com.app.statuscontrol.data.util.FirebaseConstants.USERS_COLLECTION
import com.app.statuscontrol.domain.repository.SaveSessionLocalRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserRepositoryImpl @Inject constructor(
    val saveSessionLocalRepository: SaveSessionLocalRepository
): UserRepository {

    override suspend fun createUser(user: User): Boolean {
        return try {
            var isSuccessful = false
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(user.uid)
                .set(user, SetOptions.merge())
                .addOnCompleteListener {
                    isSuccessful = it.isSuccessful
                    saveSessionLocalRepository.execute(user)
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

    override suspend fun modifyUser(user: User): Boolean {
        return try {
            var isSuccessful = false
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(user.uid)
                .set(user, SetOptions.merge())
                .addOnCompleteListener {
                    isSuccessful = it.isSuccessful
                    saveSessionLocalRepository.execute(user)
                }
                .await()
            isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUser(uid: String): User {
        return try {
            var loggedUser = User()
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener {
                    loggedUser = it.toObject(User::class.java)!!
                }
                .await()
            loggedUser
        } catch (e: Exception) {
            User()
        }
    }
}