package com.app.statuscontrol.domain.repository

import com.app.statuscontrol.domain.model.LaneStatus

interface SaveLaneLocalRepository {
    fun save(laneStatus: LaneStatus)
    fun getLane(): LaneStatus?
}