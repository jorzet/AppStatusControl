package com.app.statuscontrol.domain.interactor

import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.repository.AuthRepository
import com.app.statuscontrol.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(nick: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        val userUID = authRepository.login(nick,password)
        if (userUID.isNotEmpty()) {
            val user = userRepository.getUser(uid = userUID)
            emit(Resource.Success(user))
            emit(Resource.Finished)
        } else {
            emit(Resource.Error("Login error"))
            emit(Resource.Finished)
        }
    }
}