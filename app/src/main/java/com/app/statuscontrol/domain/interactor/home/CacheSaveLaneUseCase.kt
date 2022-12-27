package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.repository.SaveLaneLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CacheSaveLaneUseCase @Inject constructor(
    private val saveLaneLocalRepository: SaveLaneLocalRepository
) {
    suspend operator fun invoke(laneStatus: LaneStatus) : Flow<Resource<Boolean>> = flow {
        saveLaneLocalRepository.save(laneStatus)
        emit(Resource.Success(true))
    }
}