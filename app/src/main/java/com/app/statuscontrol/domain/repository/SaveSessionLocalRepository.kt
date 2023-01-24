package com.app.statuscontrol.domain.repository

import com.app.statuscontrol.domain.model.User

interface SaveSessionLocalRepository {
    fun execute(user: User)
    fun getSavedSession(): User?
    fun deleteUserSession()
    fun clearCache()
}