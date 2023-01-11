package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.data.remote.employee.FirebaseEmployeeRepository
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseEmployeeUseCase @Inject constructor(
    val employeeRepository: FirebaseEmployeeRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading)

        val employees = employeeRepository.getAllEmployees()
        if (!employees.isNullOrEmpty())
            emit(Resource.Error("Error"))
        else
            emit(Resource.Success(employees!!))
    }
}