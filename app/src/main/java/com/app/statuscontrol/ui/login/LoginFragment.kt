package com.app.statuscontrol.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.statuscontrol.databinding.FragmentLoginBinding
import com.app.statuscontrol.domain.model.LoginAction
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.ui.home.HomeActivity
import com.app.statuscontrol.utils.click
import com.app.statuscontrol.utils.onKeyEventListener
import com.app.statuscontrol.utils.setGone
import com.app.statuscontrol.utils.setVisible
import com.app.statuscontrol.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment() {
    // Binding
    private lateinit var binding: FragmentLoginBinding

    // View Model
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        setUpListener()
    }

    private fun initObservers() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
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

    private fun setUpListener() {
        binding.closeApp click {
            activity?.finish()
        }
        binding.loginButton click {
            login()
        }
        binding.registerButton click {
            showRegister()
        }
        binding.clickHereTextView click {
            sendEmail()
        }
        binding.passwordTextInputEditText onKeyEventListener { view, keyCode, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.loginButton.performClick()
                true
            } else false
        }
    }

    private fun login() {
        val nick = binding.userNickTextInputEditText.text.toString()
        val pass = binding.passwordTextInputEditText.text.toString()
        if (nick.isNotEmpty()) {
            if (pass.isNotEmpty()) {
                viewModel.login(nick, pass)
            } else {
                showError("Necesita ingresar una contraseña")
            }
        } else {
            showError("Necesita ingrear un email")
        }
    }

    private fun showHome() {
        val intent = Intent(activity, HomeActivity::class.java)
        intent.putExtra(LoginActivity.LOGIN_ACTION, LoginAction.LOGIN_ACTION)
        activity?.let{
            it.finish()
            startActivity(intent)
        }
    }

    private fun showRegister() {
        activity?.let {
            (it as LoginActivity).showRegisterFragment()
        }
    }

    private fun sendEmail() {
        if (binding.userNickTextInputEditText.text.toString().isEmpty()) {
            showError("Ingrese su nick para poder reestablecer su contraseña")
        } else {
            val emailIntent = Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "ralphmagnifico@gmail.com", null))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Password reset")
            emailIntent.putExtra(Intent.EXTRA_TEXT, getMessage())
            startActivity(Intent.createChooser(emailIntent, "Elija una app para mandar un email..."))
        }
    }

    private fun getMessage(): String {
        return "Nick account to reset: " + binding.userNickTextInputEditText.text.toString()
    }

    private fun showError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(activity, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                loading.setVisible()
                loginButton.setGone()
                registerButton.isEnabled = false
                clickHereTextView.isEnabled = false
                closeApp.isEnabled = false
            } else {
                loading.setGone()
                loginButton.setVisible()
                registerButton.isEnabled = true
                clickHereTextView.isEnabled = true
                closeApp.isEnabled = true
            }
        }
    }
}