package com.app.statuscontrol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.statuscontrol.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val authRepository: AuthRepository
): ViewModel() {

    private val _sessionState: MutableLiveData<Boolean> = MutableLiveData()
    val sessionState: LiveData<Boolean>
        get() = _sessionState

    fun validateSession() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _sessionState.value = user != null
        }
    }

}