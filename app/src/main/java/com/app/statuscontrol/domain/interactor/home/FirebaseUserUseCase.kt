package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.repository.AuthRepository
import com.app.statuscontrol.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseUserUseCase @Inject constructor(
    val authRepository: AuthRepository,
    val userRepository: UserRepository
) {

    suspend operator fun invoke(): Flow<Resource<User>> = flow {
        emit(Resource.Loading)

        val firebaseUser = authRepository.getCurrentUser()
        firebaseUser?.let {
            val user = userRepository.getUser(it.uid)
            if (user != null) {
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error("User not found"))
            }
        }
    }

}