package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.repository.LaneStatusRepository
import com.app.statuscontrol.domain.repository.SaveLaneLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseGetLaneUseCase @Inject constructor(
    private val laneStatusRepository: LaneStatusRepository,
) {
    suspend operator fun invoke(lane: String): Flow<Resource<LaneStatus>> = flow {
        emit(Resource.Loading)

        val laneStatus = laneStatusRepository.getLaneStatus(lane)
        if (laneStatus != null) {
            emit(Resource.Success(laneStatus))
        } else {
            emit(Resource.Error("Error"))
        }
    }
}