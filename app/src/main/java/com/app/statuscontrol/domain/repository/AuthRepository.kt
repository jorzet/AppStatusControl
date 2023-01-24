package com.app.statuscontrol.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(nick: String, password:String): String

    suspend fun signUp(email:String, password: String): String

    suspend fun getCurrentUser(): FirebaseUser?

    suspend fun logout()
}