package com.app.statuscontrol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.statuscontrol.domain.interactor.login.FirebaseSignUpUseCase
import com.app.statuscontrol.domain.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: FirebaseSignUpUseCase
) :  ViewModel() {

    private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private val _signUpState: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val signUpState: LiveData<Resource<Boolean>>
        get() = _signUpState

    fun signUp(email: String, password: String, nick: String, name: String, isEmployee: Boolean, deviceId: String) {
        viewModelScope.launch {
            if(password.length >= 6) {
                signUpUseCase(email, password, nick, name, isEmployee, deviceId).onEach { state ->
                    _signUpState.value = state
                }.launchIn(viewModelScope)
            } else {
                _signUpState.value = Resource.Error("La contraseña necesita tener 6 o mas caracteres")
            }
        }
    }

    fun validateEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }
}