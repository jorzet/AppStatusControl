package com.app.statuscontrol.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.statuscontrol.databinding.ActivityHomeBinding
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.utils.click
import com.app.statuscontrol.utils.showOffline
import com.app.statuscontrol.utils.showOnline
import com.app.statuscontrol.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeActivity: AppCompatActivity(), TextToSpeech.OnInitListener {
    // Binding
    private lateinit var binding: ActivityHomeBinding

    // View model
    private val viewModel: HomeViewModel by viewModels()

    // Receiver
    private lateinit var mNetworkChangeReceiver: NetworkChangeReceiver

    // Text to speech
    private lateinit var textToSpeech: TextToSpeech

    private var isConnected: Boolean = true


    companion object {
        const val HOME_ACTION = "home_action"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setUpComponents()
        setUpListeners()
        initObservers()
        //registerNetworkBroadcastForNougat()

        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, LaneStatusFragment())
            .commit()
    }

    private fun setUpComponents() {
        textToSpeech = TextToSpeech(this, this)
        viewModel.getUserLane()
    }

    private fun initObservers() {
        viewModel.userLaneState.observe(this) { state ->
            when(state) {
                is Resource.Success -> {
                    binding.welcomeTextView.text = state.data.lane
                    if (state.data.status) {
                        binding.onlineImageView.showOnline()
                    } else {
                        binding.onlineImageView.showOffline()
                    }
                    viewModel.getLane(state.data)
                }
                else -> Unit
            }
        }

        viewModel.laneState.observe(this) { state ->
            when(state) {
                is Resource.Success -> {
                    viewModel.saveLaneToLocal(state.data)
                }
                else -> Unit
            }
        }

        viewModel.modifyLaneState.observe(this) { state ->
            when(state) {
                is Resource.Success -> {
                    if (state.data.status) {
                        binding.onlineImageView.showOnline()
                    } else {
                        binding.onlineImageView.showOffline()
                    }
                }
                else -> Unit
            }
        }
    }

    private fun registerNetworkBroadcastForNougat() {
        mNetworkChangeReceiver = NetworkChangeReceiver(object : ConnectionNetworkListener {
            override fun onConnected() {
                binding.onlineImageView.showOnline()
                showError("Conectado")
                isConnected = true
            }
            override fun onDisconnected() {
                binding.onlineImageView.showOffline()
                showError("Desconectado")
                isConnected = false
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mNetworkChangeReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(
                mNetworkChangeReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    fun showUserStatus() {
        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, UserStatusFragment())
            .addToBackStack("user_status_fragment")
            .commit()
    }

    private fun setUpListeners() {
        binding.closeApp click {
            finish()
            viewModel.logout()
        }
        binding.btnChangeStatus click {
            viewModel.changeLaneStatus()
        }
    }

    fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    /**
     * Connection Listener
     */
    interface ConnectionNetworkListener {
        fun onConnected()
        fun onDisconnected()
    }

    /**
     * Connection BroadcastReceiver
     */
    class NetworkChangeReceiver(): BroadcastReceiver() {
        private lateinit var mConnectionNetworkListener: ConnectionNetworkListener

        constructor(connectionNetworkListener: ConnectionNetworkListener): this() {
            mConnectionNetworkListener = connectionNetworkListener
        }

        override fun onReceive(context: Context?, p1: Intent?) {
            try {
                val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.activeNetworkInfo
                val isConnected = netInfo != null && netInfo.isConnected
                if (isConnected) {
                    mConnectionNetworkListener.onConnected()
                } else {
                    mConnectionNetworkListener.onDisconnected()
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()

            }
        }
    }

    override fun onInit(status: Int) {
        if ( status == TextToSpeech.SUCCESS ) {
            val result = textToSpeech.setLanguage( Locale.getDefault() );

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}