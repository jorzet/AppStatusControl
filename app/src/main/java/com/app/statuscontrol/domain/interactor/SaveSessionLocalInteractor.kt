package com.app.statuscontrol.domain.interactor;

import android.preference.PreferenceManager;
import com.app.statuscontrol.FirebaseApp
import com.app.statuscontrol.domain.model.User;
import com.google.gson.Gson

class SaveSessionLocalInteractor {

    companion object {
        private const val USER_KEY = "user_key"
    }

    fun execute(user:User) {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            val editor = preferences.edit()
            val json: String = Gson().toJson(user)
            editor.putString(USER_KEY, json)
            editor.apply()
        }
    }

    fun getSavedSession(): User? {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            val userJson = preferences.getString(USER_KEY, "")
            val user = Gson().fromJson(userJson, User::class.java)
            return user
        }
        return null
    }

    fun deleteUserSession() {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            preferences.edit().remove(USER_KEY)
        }
    }

    fun clearCache() {
        FirebaseApp.INSTANCE?.baseContext?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            preferences.edit().clear().commit()
        }
    }
}