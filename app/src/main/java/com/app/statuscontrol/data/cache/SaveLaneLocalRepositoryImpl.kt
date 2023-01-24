package com.app.statuscontrol.data.cache

import android.preference.PreferenceManager
import com.app.statuscontrol.FirebaseApp
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.repository.SaveLaneLocalRepository
import com.google.gson.Gson
import javax.inject.Inject

class SaveLaneLocalRepositoryImpl @Inject constructor(

): SaveLaneLocalRepository {

    companion object {
        private const val LANE_KEY = "lane_key"
    }

    override fun save(laneStatus: LaneStatus) {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            val editor = preferences.edit()
            val json: String = Gson().toJson(laneStatus)
            editor.putString(LANE_KEY, json)
            editor.apply()
        }
    }

    override fun getLane(): LaneStatus? {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            val laneJson = preferences.getString(LANE_KEY, "")
            val lane = Gson().fromJson(laneJson, LaneStatus::class.java)
            return lane
        }
        return null
    }
}