package com.app.statuscontrol.di


import com.app.statuscontrol.data.cache.SaveLaneLocalRepositoryImpl
import com.app.statuscontrol.data.remote.FirebaseAuthRepositoryImpl
import com.app.statuscontrol.data.remote.FirebaseLaneStatusRepositoryImp
import com.app.statuscontrol.data.remote.FirebaseUserStatusRepositoryImpl
import com.app.statuscontrol.data.remote.FirestoreUserRepositoryImpl
import com.app.statuscontrol.data.cache.SaveSessionLocalRepositoryImpl
import com.app.statuscontrol.domain.repository.*
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
    abstract fun bindLaneStatusRepository(laneStatusRepository: FirebaseLaneStatusRepositoryImp): LaneStatusRepository

    @Binds
    abstract fun bindUserStatusRepository(userStatusRepository: FirebaseUserStatusRepositoryImpl): UserStatusRepository

    @Binds
    abstract fun bindSaveSessionLocalRepository(saveSessionLocalRepository: SaveSessionLocalRepositoryImpl): SaveSessionLocalRepository

    @Binds
    abstract fun bindSaveLaneLocalRepository(saveLaneLocalRepository: SaveLaneLocalRepositoryImpl): SaveLaneLocalRepository

}