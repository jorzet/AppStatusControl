package com.app.statuscontrol.domain.interactor.login

import com.app.statuscontrol.data.cache.SaveSessionLocalRepositoryImpl
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.repository.AuthRepository
import com.app.statuscontrol.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseLogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val saveSessionLocalInteractor: SaveSessionLocalRepositoryImpl
) {

    suspend operator fun invoke(): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        val firebaseUser = authRepository.getCurrentUser()
        if (firebaseUser != null) {
            val user = userRepository.getUser(uid = firebaseUser.uid)
            if (user != null) {
                user.status = false
                userRepository.modifyUser(user)
                authRepository.logout()
                saveSessionLocalInteractor.deleteUserSession()

                emit(Resource.Success(user))
                emit(Resource.Finished)
            } else {
                emit(Resource.Error("User not found"))
                emit(Resource.Finished)
            }
        } else {
            emit(Resource.Error("Logout error"))
            emit(Resource.Finished)
        }
    }
}