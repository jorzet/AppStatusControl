package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.repository.LaneStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseSaveLaneStatusUseCase @Inject constructor(
    private val laneStatusRepository: LaneStatusRepository,
) {
    suspend operator fun invoke(laneStatus: LaneStatus) : Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        laneStatusRepository.saveLaneStatus(laneStatus)
        emit(Resource.Success(true))
    }
}