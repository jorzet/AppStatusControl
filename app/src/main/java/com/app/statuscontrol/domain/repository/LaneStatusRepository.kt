package com.app.statuscontrol.domain.repository

import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource

interface LaneStatusRepository {
    suspend fun getAllLaneStatus(): Resource<List<LaneStatus>>
}