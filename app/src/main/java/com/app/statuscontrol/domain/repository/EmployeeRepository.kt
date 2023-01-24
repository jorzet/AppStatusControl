package com.app.statuscontrol.domain.repository

import com.app.statuscontrol.domain.model.User

interface EmployeeRepository {
    suspend fun getAllEmployees(): List<User>?
}