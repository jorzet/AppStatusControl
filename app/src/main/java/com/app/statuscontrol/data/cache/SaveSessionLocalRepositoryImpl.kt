package com.app.statuscontrol.data.cache;

import android.preference.PreferenceManager;
import com.app.statuscontrol.FirebaseApp
import com.app.statuscontrol.domain.model.User;
import com.app.statuscontrol.domain.repository.SaveSessionLocalRepository
import com.google.gson.Gson
import javax.inject.Inject

class SaveSessionLocalRepositoryImpl @Inject constructor(

): SaveSessionLocalRepository {

    companion object {
        private const val USER_KEY = "user_key"
    }

    override fun execute(user:User) {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            val editor = preferences.edit()
            val json: String = Gson().toJson(user)
            editor.putString(USER_KEY, json)
            editor.apply()
        }
    }

    override fun getSavedSession(): User? {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            val userJson = preferences.getString(USER_KEY, "")
            val user = Gson().fromJson(userJson, User::class.java)
            return user
        }
        return null
    }

    override fun deleteUserSession() {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            preferences.edit().remove(USER_KEY).commit()
        }
    }

    override fun clearCache() {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            preferences.edit().clear().commit()
        }
    }
}