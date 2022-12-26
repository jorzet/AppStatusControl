package com.app.statuscontrol.di


import com.app.statuscontrol.data.remote.FirebaseAuthRepositoryImpl
import com.app.statuscontrol.data.remote.FirebaseLaneStatusRepositoryImp
import com.app.statuscontrol.data.remote.FirestoreUserRepositoryImpl
import com.app.statuscontrol.domain.repository.AuthRepository
import com.app.statuscontrol.domain.repository.LaneStatusRepository
import com.app.statuscontrol.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(authRepository: FirebaseAuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindUserRepository(userRepository: FirestoreUserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindLaneStatusRepository(notesRepository: FirebaseLaneStatusRepositoryImp): LaneStatusRepository

}