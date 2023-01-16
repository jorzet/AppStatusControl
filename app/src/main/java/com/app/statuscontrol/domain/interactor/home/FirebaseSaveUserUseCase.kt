package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseSaveUserUseCase @Inject constructor(
    val userRepository: UserRepository
) {

    suspend operator fun invoke(user: User?): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)

        if (user != null)
            emit(Resource.Success(true))
        else
            emit(Resource.Error("Error"))

    }

}