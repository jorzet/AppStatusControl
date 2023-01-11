package com.app.statuscontrol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.statuscontrol.domain.interactor.home.FirebaseEmployeeUseCase
import com.app.statuscontrol.domain.interactor.home.FirebaseSaveLaneStatusUseCase
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateLaneViewModel @Inject constructor(
    private val saveLaneStatus: FirebaseSaveLaneStatusUseCase,
    private val employeeUseCase: FirebaseEmployeeUseCase
): ViewModel() {

    private val _saveLaneState: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val saveLaneState: LiveData<Resource<Boolean>>
        get() = _saveLaneState

    private val _employeesState: MutableLiveData<Resource<List<User>>> = MutableLiveData()
    val employeesState: LiveData<Resource<List<User>>>
        get() = _employeesState

    fun save(laneStatus: LaneStatus) {
        viewModelScope.launch {
            saveLaneStatus(laneStatus).onEach { lane ->
                _saveLaneState.value = lane
            }.launchIn(viewModelScope)
        }
    }

    fun getEmployees() {
        viewModelScope.launch {
            employeeUseCase().onEach {
                _employeesState.value = it
            }.launchIn(viewModelScope)
        }
    }
}