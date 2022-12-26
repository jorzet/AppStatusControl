package com.app.statuscontrol.data.remote

import com.app.statuscontrol.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthRepository {

    override suspend fun login(nick: String, password: String): String {
        return try {
            var userUID = ""
            val nickMail = "$nick@gmail.com"
            firebaseAuth.signInWithEmailAndPassword(nickMail, password)
                .addOnSuccessListener {
                    userUID = it.user?.uid ?: ""
                }
                .await()
            userUID
        } catch (e: Exception) {
            ""
        }
    }

    override suspend fun signUp(nick: String, password: String): String {
        return try {
            var userUID = ""
            val nickMail = "$nick@gmail.com"
            firebaseAuth.createUserWithEmailAndPassword(nickMail, password)
                .addOnSuccessListener {
                    userUID = it.user?.uid ?: ""
                }
                .await()
            userUID
        } catch (e: Exception) {
            ""
        }
    }
}