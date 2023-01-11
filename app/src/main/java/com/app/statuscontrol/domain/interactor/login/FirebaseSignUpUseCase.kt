package com.app.statuscontrol.domain.interactor.login

import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.model.UserType
import com.app.statuscontrol.domain.repository.AuthRepository
import com.app.statuscontrol.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(email: String, password: String, nick: String, name: String, isEmployee: Boolean): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val userUID = authRepository.signUp(nick,password)
        if (userUID.isNotEmpty()) {
            val userType = if (isEmployee) UserType.EMPLOYEE.userType else UserType.CONSUMER.userType
            userRepository.createUser(
                User(
                    uid = userUID,
                    email = email,
                    password = password,
                    nick = nick,
                    name = name,
                    status = true,
                    userType = userType
                )
            )

            emit(Resource.Success(true))
        } else {
            emit(Resource.Error("Sign up error"))
        }
    }
}