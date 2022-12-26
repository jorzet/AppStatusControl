package com.app.statuscontrol.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import com.app.statuscontrol.BuildConfig
import com.app.statuscontrol.R
import com.app.statuscontrol.databinding.ActivitySplashBinding
import com.app.statuscontrol.ui.login.LoginActivity
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity: ComponentActivity() {

    companion object {
        private const val TIMEOUT: Long = 2000
    }

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.versionTextView.text = getString(R.string.app_name) + " Ver. " + BuildConfig.VERSION_NAME

        Handler(Looper.getMainLooper()).postDelayed({
            showLoginActivity()
        },
            TIMEOUT
        )
    }

    private fun showLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        this.finish()
        startActivity(intent)
    }
}