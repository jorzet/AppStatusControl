package com.app.statuscontrol.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.app.statuscontrol.BuildConfig
import com.app.statuscontrol.R
import com.app.statuscontrol.databinding.ActivitySplashBinding
import com.app.statuscontrol.ui.home.HomeActivity
import com.app.statuscontrol.ui.login.LoginActivity
import com.app.statuscontrol.viewmodel.HomeViewModel
import com.app.statuscontrol.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class SplashActivity: ComponentActivity() {

    companion object {
        private const val TIMEOUT: Long = 2000
    }

    private lateinit var binding: ActivitySplashBinding

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initObservers()
        binding.versionTextView.text = getString(R.string.app_name) + " Ver. " + BuildConfig.VERSION_NAME

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.validateSession()
        },
            TIMEOUT
        )
    }

    private fun initObservers() {
        viewModel.sessionState.observe(this) {
            if (it) {
                showHomeActivity()
            } else {
                showLoginActivity()
            }
        }
    }

    private fun showLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        this.finish()
        startActivity(intent)
    }

    private fun showHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        this.finish()
        startActivity(intent)
    }
}