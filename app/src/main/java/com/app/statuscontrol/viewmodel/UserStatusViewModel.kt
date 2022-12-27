package com.app.statuscontrol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.statuscontrol.domain.interactor.home.FirebaseGetAllUserStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStatusViewModel @Inject constructor(
    private val getAllUserStatussRealTimeUseCase: FirebaseGetAllUserStatus
): ViewModel() {

    private val _userListState: MutableLiveData<Resource<List<User>>> = MutableLiveData()
    val userListState: LiveData<Resource<List<User>>>
        get() = _userListState

    fun getAllUSerStatus() {
        viewModelScope.launch {
            getAllUserStatussRealTimeUseCase().onEach {
                _userListState.value = it
            }.launchIn(viewModelScope)
        }
    }
}