package com.app.statuscontrol.domain.interactor.login

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

    suspend operator fun invoke(nick: String, password: String, deviceId: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        val userUID = authRepository.login(nick,password)
        if (userUID.isNotEmpty()) {
            val user = userRepository.getUser(uid = userUID)
            if (user.deviceID == deviceId || user.deviceID.isNullOrEmpty()) {
                user.deviceID = deviceId
                user.status = true
                userRepository.modifyUser(user)
                emit(Resource.Success(user))
                emit(Resource.Finished)
            } else {
                emit(Resource.Error("Solo se puede iniciar sesion en un solo dispositivo, devincula para poder iniciar sesion"))
                emit(Resource.Finished)
            }
        } else {
            emit(Resource.Error("Login error - El usuario no existe"))
            emit(Resource.Finished)
        }
    }
}