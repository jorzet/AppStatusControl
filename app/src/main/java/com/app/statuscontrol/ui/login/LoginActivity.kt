package com.app.statuscontrol.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.statuscontrol.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    companion object {
        const val LOGIN_ACTION = "login_action"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, LoginFragment())
            .commit()
    }

    fun showRegisterFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, RegisterFragment())
            .addToBackStack("register_fragment")
            .commit()
    }
}