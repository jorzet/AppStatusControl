package com.app.statuscontrol.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.statuscontrol.databinding.FragmentRegisterBinding
import com.app.statuscontrol.domain.model.LoginAction
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.ui.home.HomeActivity
import com.app.statuscontrol.utils.click
import com.app.statuscontrol.utils.onKeyEventListener
import com.app.statuscontrol.utils.setGone
import com.app.statuscontrol.utils.setVisible
import com.app.statuscontrol.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        setUpListeners()
    }

    private fun initObservers() {
        viewModel.signUpState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is Resource.Success -> {
                    handleLoading(isLoading = false)
                    showHome()
                }
                is Resource.Error -> {
                    handleLoading(isLoading = false)
                    showError(state.message)
                }
                is Resource.Loading -> handleLoading(isLoading = true)
                else -> Unit
            }
        }
    }

    private fun setUpListeners() {
        binding.registerButton click {
            register()
        }
        binding.cancelButton click {
            goBack()
        }
        binding.backImageView click {
            goBack()
        }
        binding.closeApp click {
            activity?.finish()
        }
        binding.completeNameTextInputEditText onKeyEventListener { _, keyCode, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.registerButton.performClick()
                true
            } else false
        }
    }

    private fun register() {
        val email = binding.userTextInputEditText.text.toString()
        val pass = binding.passwordTextInputEditText.text.toString()
        val nick = binding.nickTextInputEditText.text.toString()
        val completeName = binding.completeNameTextInputEditText.text.toString()
        val isEmployee = binding.cbIsEmployee.isChecked
        if (email.isNotEmpty()) {
            if (viewModel.validateEmail(email)) {
                if (pass.isNotEmpty()) {
                    if (nick.isNotEmpty()) {
                        if (completeName.isNotEmpty()) {
                            viewModel.signUp(email, pass, nick, completeName, isEmployee)
                        } else {
                            showError("Necesita ingresar su nombre completo")
                        }
                    } else {
                        showError("Necesita ingresar un nick")
                    }
                } else {
                    showError("Necesita ingresar una contraseña")
                }
            } else {
                showError("Ingrese un email valido ejemplo@ejemplo.com")
            }
        } else {
            showError("Necesita ingresar un email")
        }
    }

    private fun showHome() {
        val intent = Intent(activity, HomeActivity::class.java)
        intent.putExtra(LoginActivity.LOGIN_ACTION, LoginAction.REGISTER_ACTION)
        activity?.let {
            it.finish()
            it.startActivity(intent)
        }
    }

    private fun goBack() {
        activity?.onBackPressed()
    }

    private fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_LONG).show()
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                loading.setVisible()
                registerButton.setGone()
            } else {
                loading.setGone()
                registerButton.setVisible()
            }
        }
    }
}