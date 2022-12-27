package com.app.statuscontrol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.statuscontrol.domain.interactor.home.*
import com.app.statuscontrol.domain.interactor.login.FirebaseLogoutUseCase
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: FirebaseLogoutUseCase,
    private val userUseCase: FirebaseUserUseCase,
    private val cacheSaveLaneUseCase: CacheSaveLaneUseCase,
    private val getLaneUseCase: FirebaseGetLaneUseCase,
    private val changeLaneStatusUseCase: FirebaseChangeLaneStatusUseCase
): ViewModel() {
    private val _logoutState: MutableLiveData<Resource<User>> = MutableLiveData()
    val logoutState: LiveData<Resource<User>>
        get() = _logoutState

    private val _userLaneState: MutableLiveData<Resource<User>> = MutableLiveData()
    val userLaneState: LiveData<Resource<User>>
        get() = _userLaneState

    private val _laneState: MutableLiveData<Resource<LaneStatus>> = MutableLiveData()
    val laneState: LiveData<Resource<LaneStatus>>
        get() = _laneState

    private val _modifyLaneState: MutableLiveData<Resource<LaneStatus>> = MutableLiveData()
    val modifyLaneState: LiveData<Resource<LaneStatus>>
        get() = _modifyLaneState

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().onEach { state ->
                _logoutState.value = state
            }.launchIn(viewModelScope)
        }
    }

    fun getUserLane() {
        viewModelScope.launch {
            userUseCase().onEach { user ->
                _userLaneState.value = user
            }.launchIn(viewModelScope)
        }
    }

    fun getLane(user: User) {
        viewModelScope.launch {
            getLaneUseCase(user.laneId).onEach { lane ->
                _laneState.value = lane
            }.launchIn(viewModelScope)
        }
    }

    fun saveLaneToLocal(laneStatus: LaneStatus) {
        viewModelScope.launch {
            cacheSaveLaneUseCase(laneStatus).onEach {

            }
        }
    }

    fun changeLaneStatus() {
        viewModelScope.launch {
            changeLaneStatusUseCase().onEach { lane ->
                _modifyLaneState.value = lane
            }.launchIn(viewModelScope)
        }
    }
}