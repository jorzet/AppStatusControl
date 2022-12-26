package com.app.statuscontrol.data.remote

import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.data.util.FirebaseConstants.LANE_STATUS_COLLECTION
import com.app.statuscontrol.domain.repository.LaneStatusRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseLaneStatusRepositoryImp @Inject constructor(

): LaneStatusRepository  {

    override suspend fun getAllLaneStatus(): Resource<List<LaneStatus>> {
        return try {
            val notesList = FirebaseFirestore.getInstance().collection(LANE_STATUS_COLLECTION)
                .get()
                .await()
                .toObjects(LaneStatus::class.java)

            Resource.Success(notesList)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

}