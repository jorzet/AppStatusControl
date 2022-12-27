package com.app.statuscontrol.domain.repository

import com.app.statuscontrol.domain.model.User

interface UserRepository {

    suspend fun createUser(user: User): Boolean

    suspend fun modifyUser(user: User): Boolean

    suspend fun getUser(uid: String): User
}