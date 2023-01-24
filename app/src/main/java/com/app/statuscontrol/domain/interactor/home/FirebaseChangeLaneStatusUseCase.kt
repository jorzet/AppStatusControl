package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.repository.LaneStatusRepository
import com.app.statuscontrol.domain.repository.SaveLaneLocalRepository
import com.app.statuscontrol.domain.repository.SaveSessionLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class FirebaseChangeLaneStatusUseCase @Inject constructor(
    private val laneStatusRepository: LaneStatusRepository,
    private val saveLaneLocalRepository: SaveLaneLocalRepository,
    private val saveSessionLocalRepository: SaveSessionLocalRepository
) {
    suspend operator fun invoke(): Flow<Resource<LaneStatus>> = flow {
        emit(Resource.Loading)

        val lane = saveLaneLocalRepository.getLane()
        val user = saveSessionLocalRepository.getSavedSession()
        val currentTime: Date = Calendar.getInstance().time

        lane?.let {
            user?.let {
                lane.modifiedBy = user.name
                lane.userUid = user.uid
                lane.id = user.laneId
            }

            lane.status = !lane.status
            lane.lastModification = currentTime.toString()
            laneStatusRepository.saveLaneStatus(lane)
            saveLaneLocalRepository.save(lane)
            emit(Resource.Success(lane))
        }
    }
}