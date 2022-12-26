package com.app.statuscontrol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.statuscontrol.domain.interactor.FirebaseGetAllLaneUseCase
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaneStatusViewModel @Inject constructor(
    private val getAllNotesRealTimeUseCase: FirebaseGetAllLaneUseCase
): ViewModel() {

    private val _laneListState: MutableLiveData<Resource<List<LaneStatus>>> = MutableLiveData()
    val laneListState: LiveData<Resource<List<LaneStatus>>>
        get() = _laneListState


    fun getAllLaneStatus() {
        viewModelScope.launch {
            getAllNotesRealTimeUseCase().onEach {
                _laneListState.value = it
            }.launchIn(viewModelScope)
        }
    }
}