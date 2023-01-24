package com.app.statuscontrol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.statuscontrol.domain.interactor.home.FirebaseGetAllLaneUseCase
import com.app.statuscontrol.domain.interactor.home.FirebaseUserUseCase
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaneStatusViewModel @Inject constructor(
    private val getAllLaneStatusRealTimeUseCase: FirebaseGetAllLaneUseCase,
    private val userUseCase: FirebaseUserUseCase,
    ): ViewModel() {

    private val _laneListState: MutableLiveData<Resource<List<LaneStatus>>> = MutableLiveData()
    val laneListState: LiveData<Resource<List<LaneStatus>>>
        get() = _laneListState

    private val _userLaneState: MutableLiveData<Resource<User>> = MutableLiveData()
    val userLaneState: LiveData<Resource<User>>
        get() = _userLaneState

    fun getAllLaneStatus() {
        viewModelScope.launch {
            getAllLaneStatusRealTimeUseCase().onEach { lane ->
                _laneListState.value = lane
            }.launchIn(viewModelScope)
        }
    }

    fun getUser() {
        viewModelScope.launch {
            userUseCase().onEach { user ->
                _userLaneState.value = user
            }.launchIn(viewModelScope)
        }
    }
}