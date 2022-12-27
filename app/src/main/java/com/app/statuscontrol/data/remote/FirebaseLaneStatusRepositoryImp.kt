package com.app.statuscontrol.data.remote

import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.data.util.FirebaseConstants.LANE_STATUS_COLLECTION
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.repository.LaneStatusRepository
import com.app.statuscontrol.domain.repository.SaveLaneLocalRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseLaneStatusRepositoryImp @Inject constructor(
    private val saveLaneLocalRepository: SaveLaneLocalRepository
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

    override suspend fun saveLaneStatus(laneStatus: LaneStatus): Boolean {
        return try {
            var isSuccessful = false
            FirebaseFirestore.getInstance().collection(LANE_STATUS_COLLECTION)
                .document(laneStatus.id)
                .set(laneStatus, SetOptions.merge())
                .addOnCompleteListener {
                    isSuccessful = it.isSuccessful
                }
                .await()
            isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getLaneStatus(lane: String): LaneStatus? {
        return try {
            var laneStatus = LaneStatus()
            FirebaseFirestore.getInstance().collection(LANE_STATUS_COLLECTION)
                .document(lane)
                .get()
                .addOnSuccessListener {
                    laneStatus = it.toObject(LaneStatus::class.java)!!
                    saveLaneLocalRepository.save(laneStatus)
                }
                .await()
                laneStatus
        } catch (e: Exception) {
            null
        }
    }
}