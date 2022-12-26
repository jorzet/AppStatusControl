package com.app.statuscontrol.domain.repository


interface AuthRepository {
    suspend fun login(nick: String, password:String): String

    suspend fun signUp(email:String, password: String): String
}