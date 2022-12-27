package com.app.statuscontrol.domain.repository

import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User

interface UserStatusRepository {
    suspend fun getAllUserStatus(): Resource<List<User>>
}