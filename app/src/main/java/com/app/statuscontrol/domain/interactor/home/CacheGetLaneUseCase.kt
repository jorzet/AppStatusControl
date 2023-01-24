package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.repository.SaveLaneLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CacheGetLaneUseCase @Inject constructor(
    private val saveLaneLocalRepository: SaveLaneLocalRepository
) {
    suspend operator fun invoke() : Flow<Resource<LaneStatus>> = flow {
        val laneStatus = saveLaneLocalRepository.getLane()
        if (laneStatus != null) {
            emit(Resource.Success(laneStatus))
        } else {
            emit(Resource.Error("Error"))
        }
    }
}