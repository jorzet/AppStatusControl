package com.app.statuscontrol.domain.interactor.home

import com.app.statuscontrol.data.util.FirebaseConstants
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.repository.LaneStatusRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseGetAllLaneUseCase @Inject constructor(
    private val laneStatusRepository: LaneStatusRepository,
) {
    suspend operator fun invoke(): Flow<Resource<List<LaneStatus>>> = callbackFlow {
        this.trySend(Resource.Loading)

        val event = FirebaseFirestore.getInstance().collection(FirebaseConstants.LANE_STATUS_COLLECTION)

        val subscription = event.addSnapshotListener { snapshot, error ->

            if (error != null) {
                this.trySend(Resource.Error(error.message.toString())).isSuccess
                return@addSnapshotListener
            }

            if (snapshot != null) {

                val laneList = arrayListOf<LaneStatus>()
                snapshot.documents.map {
                    val laneStatus = it.toObject(LaneStatus::class.java)

                    laneStatus?.let { lane ->
                        lane.id = it.id
                        laneList.add(lane)
                    }
                }

                this.trySend(Resource.Success(laneList)).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }

}