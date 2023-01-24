package com.app.statuscontrol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.statuscontrol.domain.interactor.login.FirebaseLoginUseCase
import com.app.statuscontrol.domain.interactor.login.FirebasePasswordRecoveryUseCase
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: FirebaseLoginUseCase,
    private val recoveryUseCase: FirebasePasswordRecoveryUseCase
) :  ViewModel() {

    private val _loginState: MutableLiveData<Resource<User>> = MutableLiveData()
    val loginState: LiveData<Resource<User>>
        get() = _loginState

    private val _recoveryPasswordState: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val recoveryPasswordState: LiveData<Resource<Boolean>>
        get() = _recoveryPasswordState

    fun login(email: String, password: String, deviceId: String) {
        viewModelScope.launch {
            loginUseCase(email, password, deviceId).onEach { state ->
                _loginState.value = state
            }.launchIn(viewModelScope)
        }
    }

    fun recoveryPassword(email: String) {
        viewModelScope.launch {
            recoveryUseCase(email).onEach { state ->
                _recoveryPasswordState.value = state
            }.launchIn(viewModelScope)
        }
    }
}